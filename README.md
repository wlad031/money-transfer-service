## Money Transfer Service

![Travis (.org) branch](https://img.shields.io/travis/wlad031/money-transfer-service/master) 
![Codecov branch](https://img.shields.io/codecov/c/gh/wlad031/money-transfer-service/master)

#### Prerequisites

1. Maven 3
2. JDK 11

#### Download

``
git clone git@github.com:wlad031/money-transfer-service.git
``

#### Build

``
mvn package
``

#### Run

``
java -jar target/money-transfer-0.0.1.jar
``

#### Available methods

Default port is `8080`.

Here is the short description of all available endpoints.

Please use `/swagger` endpoint to get
Swagger UI, where you can find more information and also
try all the endpoints right from there.

| Method | URL | Description |
|--------|-----|-------------|
| GET    | `​/account​/{id}`              | Returns account details by it's ID |
| GET    | `​/account`                   | Returns all registered account IDs |
| POST   | `​/account`                   | Creates and registers new account |
| GET    | `​/account​/{id}​/transactions` | Returns all transactions for the given account |
| GET    | `​/transaction​/{id}`          | Returns transaction details by it's ID | 
| POST   | `​/transaction`               | Creates new transaction |

Moreover, all available endpoints are in the `requests.http` file. It could be really
useful when using JetBrains IDEs.

##### Basic scenario

Basic scenario is:
1. create a couple of accounts
2. deposit some money to the accounts
   using transactions with null senderId
3. create some transactions between accounts
4. check account balances
