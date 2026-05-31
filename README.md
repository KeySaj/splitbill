# SplitBill

SplitBill is a mobile application for splitting shared expenses between friends.

## Authors

- Jan Mielniczek
- Jakub Wajman

## Project description

SplitBill allows users to create groups, add shared expenses, and calculate who owes money to whom.

## Architecture

- Mobile App (Android / Kotlin)
- Auth Service
- Expense Service
- PostgreSQL

## Tech stack

- Mobile: Kotlin, Jetpack Compose
- Backend: FastAPI
- Database: PostgreSQL
- Local storage: Room

## Communication

- Client -> API: HTTP REST
- Service -> Service: HTTP REST
- Authentication: JWT

## API documentation

API documentation will be generated automatically using OpenAPI/Swagger.

## Status

Work in progress⌛

# API endpoints

## Auth Service - port 8000

POST /auth/register
POST /auth/login
GET /auth/me?token={token}

## Expense Service - port 8001

POST /groups
GET /groups
GET /groups/{group_id}
POST /groups/{group_id}/expenses
GET /groups/{group_id}/expenses
GET /groups/{group_id}/settlements
