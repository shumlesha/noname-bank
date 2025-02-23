## Запросы

1. POST `/api/account/query`

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
  "isCredit": false
}
```

**Error**

```json
{
  "message": "Указанный счет не найден",
  "statusCode": 401
}
```

2. POST `/api/account/query/list`

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
      "isCredit": false
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