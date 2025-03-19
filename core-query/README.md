## Ядро на чтение

### Запросы

1. POST `/api/query/account`

**body**

```json
{
  "clientId": "3fe0f380-37f5-4bef-b732-d624ee45eff0",
  "accountId": "225ff63f-6cb4-4b66-9f76-dcb187462670"
}
```

**Success**

```json
{
  "id": "225ff63f-6cb4-4b66-9f76-dcb187462670",
  "creationTimestamp": "2025-02-23 14:01:00",
  "blockedTimestamp": null,
  "closedTimestamp": "2025-02-23 14:07:09",
  "clientId": "225ff63f-6cb4-4b66-9f76-dcb187462670",
  "number": "3966930656874947",
  "balance": 0,
  "isCredit": false,
  "currency": "RUR"
}
```

**Error**

```json
{
  "message": "Указанный счет не найден",
  "statusCode": 401
}
```

2. POST `/api/query/account/list`

**body**

```json
{
  "clientId": "3fe0f380-37f5-4bef-b732-d624ee45eff0"
}
```

**Success**

```json
{
  "data": [
    {
      "id": "225ff63f-6cb4-4b66-9f76-dcb187462670",
      "creationTimestamp": "2025-02-23 14:01:00",
      "blockedTimestamp": null,
      "closedTimestamp": "2025-02-23 14:07:09",
      "clientId": "225ff63f-6cb4-4b66-9f76-dcb187462670",
      "number": "3966930656874947",
      "balance": 0,
      "isCredit": false,
      "currency": "RUR"
    }
  ]
}
```

**Error**

```json
{
  "message": "Произошла ошибка",
  "statusCode": 500
}
```

3. POST `/api/query/transaction/client` (все операции клиента)

**body**

```json
{
  "clientId": "1eaef959-bf10-40b2-80d9-9faacfdb13e0"
}
```

**Success**

```json
{
  "transactions": [
    {
      "id": "4ac9c349-eb8e-4f1d-afd2-2b8cc199fad1",
      "transactionTimestamp": "2025-03-01 09:27:20",
      "accountFrom": "c2dfd101-a169-4e69-8078-7806c4683683",
      "accountTo": "0d3ef08c-b2eb-4c28-9788-8715225e1a04",
      "amount": 100,
      "clientId": "1eaef959-bf10-40b2-80d9-9faacfdb13e0"
    },
    {
      "id": "1b507a23-2816-4c95-a4d9-4409f494a606",
      "transactionTimestamp": "2025-03-01 09:37:06",
      "accountFrom": "c2dfd101-a169-4e69-8078-7806c4683683",
      "accountTo": "0d3ef08c-b2eb-4c28-9788-8715225e1a04",
      "amount": 100,
      "clientId": "1eaef959-bf10-40b2-80d9-9faacfdb13e0"
    },
    {
      "id": "1a05b670-5fa1-423b-bc21-f31708fb7c44",
      "transactionTimestamp": "2025-03-01 09:37:18",
      "accountFrom": "0d3ef08c-b2eb-4c28-9788-8715225e1a04",
      "accountTo": "c2dfd101-a169-4e69-8078-7806c4683683",
      "amount": 1003,
      "clientId": "1eaef959-bf10-40b2-80d9-9faacfdb13e0"
    }
  ]
}
```

**Error**

```json
{
  "message": "Произошла ошибка",
  "statusCode": 500
}
```

4. POST `/api/query/transaction`

**body**

```json
{
  "clientId": "1eaef959-bf10-40b2-80d9-9faacfdb13e0",
  "transactionId": "4ac9c349-eb8e-4f1d-afd2-2b8cc199fad1"
}
```

**Success**

```json
{
  "id": "4ac9c349-eb8e-4f1d-afd2-2b8cc199fad1",
  "transactionTimestamp": "2025-03-01 09:27:20",
  "accountFrom": "c2dfd101-a169-4e69-8078-7806c4683683",
  "accountTo": "0d3ef08c-b2eb-4c28-9788-8715225e1a04",
  "amount": 100,
  "clientId": "1eaef959-bf10-40b2-80d9-9faacfdb13e0"
}
```

**Error**

```json
{
  "message": "Указанная транзакция не найдена",
  "statusCode": 401
}
```

5. POST `/api/query/account/all` (получение всех счетов для сотрудника)

**body**

```json
{
  "size": 1,
  "offset": 0
}
```

**Success**

```json
{
  "data": [
    {
      "id": "0d3ef08c-b2eb-4c28-9788-8715225e1a04",
      "blockedTimestamp": null,
      "closedTimestamp": null,
      "clientId": "0d3ef08c-b2eb-4c28-9788-8715225e1a04",
      "number": "5440613954528454",
      "balance": 2203,
      "isCredit": false,
      "currency": "RUR"
    }
  ],
  "page": 0,
  "pageSize": 1
}
```

**Error**

```json
{
  "message": "Произошла ошибка",
  "statusCode": 500
}
```

6. POST `/api/query/transaction/account` (получение всех транзакций счета)

**body**

```json
{
  "accountId": "0d3ef08c-b2eb-4c28-9788-8715225e1a04"
}
```

**Success**

```json
{
  "transactions": [
    {
      "id": "4ac9c349-eb8e-4f1d-afd2-2b8cc199fad1",
      "transactionTimestamp": "2025-03-01 09:27:20",
      "accountFrom": "c2dfd101-a169-4e69-8078-7806c4683683",
      "accountTo": "0d3ef08c-b2eb-4c28-9788-8715225e1a04",
      "amount": 100,
      "clientId": "1eaef959-bf10-40b2-80d9-9faacfdb13e0"
    },
    {
      "id": "1b507a23-2816-4c95-a4d9-4409f494a606",
      "transactionTimestamp": "2025-03-01 09:37:06",
      "accountFrom": "c2dfd101-a169-4e69-8078-7806c4683683",
      "accountTo": "0d3ef08c-b2eb-4c28-9788-8715225e1a04",
      "amount": 100,
      "clientId": "1eaef959-bf10-40b2-80d9-9faacfdb13e0"
    },
    {
      "id": "1a05b670-5fa1-423b-bc21-f31708fb7c44",
      "transactionTimestamp": "2025-03-01 09:37:18",
      "accountFrom": "0d3ef08c-b2eb-4c28-9788-8715225e1a04",
      "accountTo": "c2dfd101-a169-4e69-8078-7806c4683683",
      "amount": 1003,
      "clientId": "1eaef959-bf10-40b2-80d9-9faacfdb13e0"
    }
  ]
}
```

**Error**

```json
{
  "message": "Произошла ошибка",
  "statusCode": 500
}
```