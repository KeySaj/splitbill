from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel

app = FastAPI(title="SplitBill Expense Service")
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

class GroupCreate(BaseModel):
    name: str
    members: list[str]


class Group(BaseModel):
    id: int
    name: str
    members: list[str]


class ExpenseCreate(BaseModel):
    title: str
    amount: float
    paid_by: str
    participants: list[str]


class Expense(BaseModel):
    id: int
    group_id: int
    title: str
    amount: float
    paid_by: str
    participants: list[str]


class Settlement(BaseModel):
    from_user: str
    to_user: str
    amount: float


groups: list[Group] = []
expenses: list[Expense] = []

next_group_id = 1
next_expense_id = 1


@app.get("/")
def root():
    return {"message": "Expense service works"}


@app.get("/health")
def health():
    return {"status": "ok"}


@app.post("/groups", response_model=Group)
def create_group(payload: GroupCreate):
    global next_group_id

    if not payload.name:
        raise HTTPException(status_code=400, detail="Group name is required")

    if len(payload.members) < 2:
        raise HTTPException(status_code=400, detail="Group must have at least 2 members")

    group = Group(
        id=next_group_id,
        name=payload.name,
        members=payload.members
    )

    groups.append(group)
    next_group_id += 1

    return group


@app.get("/groups", response_model=list[Group])
def get_groups():
    return groups


@app.get("/groups/{group_id}", response_model=Group)
def get_group(group_id: int):
    group = find_group(group_id)
    return group


@app.post("/groups/{group_id}/expenses", response_model=Expense)
def create_expense(group_id: int, payload: ExpenseCreate):
    global next_expense_id

    group = find_group(group_id)

    if payload.amount <= 0:
        raise HTTPException(status_code=400, detail="Amount must be greater than 0")

    if payload.paid_by not in group.members:
        raise HTTPException(status_code=400, detail="Payer must be a group member")

    for participant in payload.participants:
        if participant not in group.members:
            raise HTTPException(status_code=400, detail=f"Participant {participant} is not a group member")

    if len(payload.participants) == 0:
        raise HTTPException(status_code=400, detail="Expense must have at least one participant")

    expense = Expense(
        id=next_expense_id,
        group_id=group_id,
        title=payload.title,
        amount=payload.amount,
        paid_by=payload.paid_by,
        participants=payload.participants
    )

    expenses.append(expense)
    next_expense_id += 1

    return expense


@app.get("/groups/{group_id}/expenses", response_model=list[Expense])
def get_group_expenses(group_id: int):
    find_group(group_id)
    return [expense for expense in expenses if expense.group_id == group_id]


@app.get("/groups/{group_id}/settlements", response_model=list[Settlement])
def get_settlements(group_id: int):
    group = find_group(group_id)
    group_expenses = [expense for expense in expenses if expense.group_id == group_id]

    balance = {member: 0.0 for member in group.members}

    for expense in group_expenses:
        split_amount = expense.amount / len(expense.participants)

        balance[expense.paid_by] += expense.amount

        for participant in expense.participants:
            balance[participant] -= split_amount

    debtors = []
    creditors = []

    for user, value in balance.items():
        rounded_value = round(value, 2)

        if rounded_value < 0:
            debtors.append({"user": user, "amount": abs(rounded_value)})
        elif rounded_value > 0:
            creditors.append({"user": user, "amount": rounded_value})

    settlements = []

    i = 0
    j = 0

    while i < len(debtors) and j < len(creditors):
        debtor = debtors[i]
        creditor = creditors[j]

        amount = min(debtor["amount"], creditor["amount"])

        settlements.append(
            Settlement(
                from_user=debtor["user"],
                to_user=creditor["user"],
                amount=round(amount, 2)
            )
        )

        debtor["amount"] = round(debtor["amount"] - amount, 2)
        creditor["amount"] = round(creditor["amount"] - amount, 2)

        if debtor["amount"] == 0:
            i += 1

        if creditor["amount"] == 0:
            j += 1

    return settlements


def find_group(group_id: int):
    for group in groups:
        if group.id == group_id:
            return group

    raise HTTPException(status_code=404, detail="Group not found")