## Ядро

Представляет собой сервис, который хранит внутри себя все счета клиентов, их баланс, историю всех операций. 
И не имеет графического интерфейса.

### Запросы

1. POST `/api/account/create`

**body**

```json
{
  "clientId": "3fe0f380-37f5-4bef-b732-d624ee45eff0",
  "currency": "RUR"
}
```

**Success**

```json
{
  "accountId": "810ca65b-84db-4f06-b826-f40030448ddd",
  "accountNumber": "6624770118481436",
  "clientId": "3fe0f380-37f5-4bef-b732-d624ee45eff0"
}
```

**Error**

```json
{
  "message": "Произошла ошибка",
  "statusCode": 500
}
```

2. POST `/api/account/close`

**body**

```json
{
  "clientId": "3fe0f380-37f5-4bef-b732-d624ee45eff0",
  "accountId": "8fe4c46f-cf1b-440c-b78a-e73f0f066a5e"
}
```

**Success**

```json
{
  "accountId": "8fe4c46f-cf1b-440c-b78a-e73f0f066a5e",
  "clientId": "3fe0f380-37f5-4bef-b732-d624ee45eff0",
  "accountNumber": "6135607724598258",
  "closedTimestamp": "2025-02-23 15:06:43"
}
```

**Error**

```json
{
  "message": "Указанного счета не существует",
  "statusCode": 401
}
```

3. POST `/api/transaction/create`

**body**

```json
{
  "accountFrom": "c2dfd101-a169-4e69-8078-7806c4683683",
  "accountTo": "0d3ef08c-b2eb-4c28-9788-8715225e1a04",
  "amount" : "1000"
}
```

**Success**

```json
{
  "transactionId": "1a05b670-5fa1-423b-bc21-f31708fb7c44",
  "accountTo": "0d3ef08c-b2eb-4c28-9788-8715225e1a04",
  "accountFrom": "c2dfd101-a169-4e69-8078-7806c4683683",
  "amount": 1003
}
```

**Error**

```json
{
  "message": "Указанного счета не существует",
  "statusCode": 401
}
```