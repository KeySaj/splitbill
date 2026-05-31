import os
import time

from fastapi import FastAPI, HTTPException, Depends
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from fastapi.middleware.cors import CORSMiddleware
from jose import jwt, JWTError
from pydantic import BaseModel
from sqlalchemy import create_engine, Column, Integer, String, Float, ForeignKey
from sqlalchemy.exc import OperationalError
from sqlalchemy.orm import declarative_base, sessionmaker, Session, relationship

DATABASE_URL = os.getenv(
    "DATABASE_URL",
    "postgresql://splitbill:splitbill@localhost:5432/splitbill"
)

SECRET_KEY = os.getenv("AUTH_SECRET_KEY", "splitbill-secret-key")
ALGORITHM = "HS256"
security = HTTPBearer()

engine = create_engine(DATABASE_URL)
SessionLocal = sessionmaker(bind=engine, autoflush=False, autocommit=False)
Base = declarative_base()

app = FastAPI(title="SplitBill Expense Service")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


class GroupDb(Base):
    __tablename__ = "groups"

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String, nullable=False)
    owner_email = Column(String, nullable=False, index=True)

    members = relationship("GroupMemberDb", cascade="all, delete-orphan")
    expenses = relationship("ExpenseDb", cascade="all, delete-orphan")


class GroupMemberDb(Base):
    __tablename__ = "group_members"

    id = Column(Integer, primary_key=True, index=True)
    group_id = Column(Integer, ForeignKey("groups.id"), nullable=False)
    name = Column(String, nullable=False)


class ExpenseDb(Base):
    __tablename__ = "expenses"

    id = Column(Integer, primary_key=True, index=True)
    group_id = Column(Integer, ForeignKey("groups.id"), nullable=False)
    title = Column(String, nullable=False)
    amount = Column(Float, nullable=False)
    paid_by = Column(String, nullable=False)

    participants = relationship("ExpenseParticipantDb", cascade="all, delete-orphan")


class ExpenseParticipantDb(Base):
    __tablename__ = "expense_participants"

    id = Column(Integer, primary_key=True, index=True)
    expense_id = Column(Integer, ForeignKey("expenses.id"), nullable=False)
    name = Column(String, nullable=False)


for attempt in range(10):
    try:
        Base.metadata.create_all(bind=engine)
        print("Expense database connected and tables created")
        break
    except OperationalError:
        print("Expense database not ready yet, waiting...")
        time.sleep(3)
else:
    raise Exception("Could not connect to expense database")


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


def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


def get_current_user(
    credentials: HTTPAuthorizationCredentials = Depends(security)
):
    try:
        token = credentials.credentials

        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        email = payload.get("sub")

        if email is None:
            raise HTTPException(status_code=401, detail="Invalid token")

        return email

    except JWTError:
        raise HTTPException(status_code=401, detail="Invalid token")


def group_to_response(group: GroupDb):
    return Group(
        id=group.id,
        name=group.name,
        members=[member.name for member in group.members]
    )


def expense_to_response(expense: ExpenseDb):
    return Expense(
        id=expense.id,
        group_id=expense.group_id,
        title=expense.title,
        amount=expense.amount,
        paid_by=expense.paid_by,
        participants=[participant.name for participant in expense.participants]
    )


def find_group(db: Session, group_id: int, current_user: str):
    group = (
        db.query(GroupDb)
        .filter(GroupDb.id == group_id, GroupDb.owner_email == current_user)
        .first()
    )

    if not group:
        raise HTTPException(status_code=404, detail="Group not found")

    return group


@app.get("/")
def root():
    return {"message": "Expense service works"}


@app.get("/health")
def health():
    return {"status": "ok"}


@app.post("/groups", response_model=Group)
def create_group(
    payload: GroupCreate,
    db: Session = Depends(get_db),
    current_user: str = Depends(get_current_user)
):
    if not payload.name:
        raise HTTPException(status_code=400, detail="Group name is required")

    if len(payload.members) < 2:
        raise HTTPException(status_code=400, detail="Group must have at least 2 members")

    group = GroupDb(
        name=payload.name,
        owner_email=current_user
    )

    db.add(group)
    db.commit()
    db.refresh(group)

    for member_name in payload.members:
        db.add(GroupMemberDb(group_id=group.id, name=member_name))

    db.commit()
    db.refresh(group)

    return group_to_response(group)


@app.get("/groups", response_model=list[Group])
def get_groups(
    db: Session = Depends(get_db),
    current_user: str = Depends(get_current_user)
):
    groups = db.query(GroupDb).filter(GroupDb.owner_email == current_user).all()
    return [group_to_response(group) for group in groups]


@app.get("/groups/{group_id}", response_model=Group)
def get_group(
    group_id: int,
    db: Session = Depends(get_db),
    current_user: str = Depends(get_current_user)
):
    group = find_group(db, group_id, current_user)
    return group_to_response(group)


@app.put("/groups/{group_id}", response_model=Group)
def update_group(
    group_id: int,
    payload: GroupCreate,
    db: Session = Depends(get_db),
    current_user: str = Depends(get_current_user)
):
    group = find_group(db, group_id, current_user)

    if not payload.name:
        raise HTTPException(status_code=400, detail="Group name is required")

    if len(payload.members) < 2:
        raise HTTPException(status_code=400, detail="Group must have at least 2 members")

    group.name = payload.name

    db.query(GroupMemberDb).filter(GroupMemberDb.group_id == group.id).delete()

    for member_name in payload.members:
        db.add(GroupMemberDb(group_id=group.id, name=member_name))

    db.commit()
    db.refresh(group)

    return group_to_response(group)


@app.delete("/groups/{group_id}")
def delete_group(
    group_id: int,
    db: Session = Depends(get_db),
    current_user: str = Depends(get_current_user)
):
    group = find_group(db, group_id, current_user)

    db.delete(group)
    db.commit()

    return {"message": "Group deleted successfully"}


@app.post("/groups/{group_id}/expenses", response_model=Expense)
def create_expense(
    group_id: int,
    payload: ExpenseCreate,
    db: Session = Depends(get_db),
    current_user: str = Depends(get_current_user)
):
    group = find_group(db, group_id, current_user)
    group_members = [member.name for member in group.members]

    if payload.amount <= 0:
        raise HTTPException(status_code=400, detail="Amount must be greater than 0")

    if payload.paid_by not in group_members:
        raise HTTPException(status_code=400, detail="Payer must be a group member")

    if len(payload.participants) == 0:
        raise HTTPException(status_code=400, detail="Expense must have at least one participant")

    for participant in payload.participants:
        if participant not in group_members:
            raise HTTPException(
                status_code=400,
                detail=f"Participant {participant} is not a group member"
            )

    expense = ExpenseDb(
        group_id=group.id,
        title=payload.title,
        amount=payload.amount,
        paid_by=payload.paid_by
    )

    db.add(expense)
    db.commit()
    db.refresh(expense)

    for participant in payload.participants:
        db.add(ExpenseParticipantDb(expense_id=expense.id, name=participant))

    db.commit()
    db.refresh(expense)

    return expense_to_response(expense)


@app.get("/groups/{group_id}/expenses", response_model=list[Expense])
def get_group_expenses(
    group_id: int,
    db: Session = Depends(get_db),
    current_user: str = Depends(get_current_user)
):
    group = find_group(db, group_id, current_user)

    expenses = db.query(ExpenseDb).filter(ExpenseDb.group_id == group.id).all()

    return [expense_to_response(expense) for expense in expenses]


@app.put("/expenses/{expense_id}", response_model=Expense)
def update_expense(
    expense_id: int,
    payload: ExpenseCreate,
    db: Session = Depends(get_db),
    current_user: str = Depends(get_current_user)
):
    expense = db.query(ExpenseDb).filter(ExpenseDb.id == expense_id).first()

    if not expense:
        raise HTTPException(status_code=404, detail="Expense not found")

    group = find_group(db, expense.group_id, current_user)
    group_members = [member.name for member in group.members]

    if payload.amount <= 0:
        raise HTTPException(status_code=400, detail="Amount must be greater than 0")

    if payload.paid_by not in group_members:
        raise HTTPException(status_code=400, detail="Payer must be a group member")

    if len(payload.participants) == 0:
        raise HTTPException(status_code=400, detail="Expense must have at least one participant")

    for participant in payload.participants:
        if participant not in group_members:
            raise HTTPException(
                status_code=400,
                detail=f"Participant {participant} is not a group member"
            )

    expense.title = payload.title
    expense.amount = payload.amount
    expense.paid_by = payload.paid_by

    db.query(ExpenseParticipantDb).filter(
        ExpenseParticipantDb.expense_id == expense.id
    ).delete()

    for participant in payload.participants:
        db.add(ExpenseParticipantDb(expense_id=expense.id, name=participant))

    db.commit()
    db.refresh(expense)

    return expense_to_response(expense)


@app.delete("/expenses/{expense_id}")
def delete_expense(
    expense_id: int,
    db: Session = Depends(get_db),
    current_user: str = Depends(get_current_user)
):
    expense = db.query(ExpenseDb).filter(ExpenseDb.id == expense_id).first()

    if not expense:
        raise HTTPException(status_code=404, detail="Expense not found")

    find_group(db, expense.group_id, current_user)

    db.delete(expense)
    db.commit()

    return {"message": "Expense deleted successfully"}


@app.get("/groups/{group_id}/settlements", response_model=list[Settlement])
def get_settlements(
    group_id: int,
    db: Session = Depends(get_db),
    current_user: str = Depends(get_current_user)
):
    group = find_group(db, group_id, current_user)
    group_members = [member.name for member in group.members]

    group_expenses = db.query(ExpenseDb).filter(ExpenseDb.group_id == group.id).all()

    balance = {member: 0.0 for member in group_members}

    for expense in group_expenses:
        participants = [participant.name for participant in expense.participants]

        if len(participants) == 0:
            continue

        split_amount = expense.amount / len(participants)

        balance[expense.paid_by] += expense.amount

        for participant in participants:
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