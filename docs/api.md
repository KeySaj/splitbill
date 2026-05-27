# SplitBill API

## Auth Service

Base URL: `http://localhost:8000`

- `POST /auth/register`
- `POST /auth/login`
- `GET /auth/me`

## Expense Service

Base URL: `http://localhost:8001`

- `POST /groups`
- `GET /groups`
- `GET /groups/{group_id}`
- `PUT /groups/{group_id}`
- `DELETE /groups/{group_id}`

- `POST /groups/{group_id}/expenses`
- `GET /groups/{group_id}/expenses`
- `PUT /expenses/{expense_id}`
- `DELETE /expenses/{expense_id}`

- `GET /groups/{group_id}/settlements`

## Example group

```json
{
  "name": "Wakacje Bari",
  "members": ["Jan", "Jakub"]
}

## Example expense

```json

{
  "title": "Pizza",
  "amount": 100,
  "paid_by": "Jan",
  "participants": ["Jan", "Jakub"]
}