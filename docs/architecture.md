flowchart LR
    subgraph ProductServices[Product Services]
        CardSvc[Card Service]
        P2PSvc[P2P Service]
        CryptoSvc[Crypto Service]
        EarningsSvc[Earnings Service]
    end

    Kafka[(Kafka)]

    CardSvc --> Kafka
    P2PSvc --> Kafka
    CryptoSvc --> Kafka
    EarningsSvc --> Kafka

    Aggregator[Activity Feed Aggregator Service]
    MySQL[(Activity MySQL DB)]
    Search[(OpenSearch / Elasticsearch)]
    Redis[(Redis Cache)]

    Kafka --> Aggregator
    Aggregator --> MySQL
    Aggregator --> Search
    Aggregator --> Redis

    Client[Mobile/Web App] --> API[Activity API]
    API --> Redis
    API --> Search
    API --> MySQL


sequenceDiagram
    participant User
    participant CardApp as Card Service
    participant Kafka
    participant Agg as Activity Aggregator
    participant DB as MySQL
    participant Search as OpenSearch

    User->>CardApp: Pay with card
    CardApp->>CardApp: Process & persist transaction
    CardApp-->>Kafka: Publish TransactionCreated event
    Kafka-->>Agg: Event consumed
    Agg->>DB: Insert ActivityEvent
    Agg->>Search: Index ActivityEvent


