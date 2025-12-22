
export type Transaction = {
    sourceName: string;
    amount: number;
    date: string;
    type: TransactionType; 
    payPeriod: PayPeriod;
};

export type TransactionEntity = Transaction &  {
    id: string;
};

export enum TransactionType {
    INCOME = "INCOME",
    DEBT = "DEBT"
};

export enum PayPeriod {
    WEEKLY = "WEEKLY",
    BIWEEKLY = "BIWEEKLY",
    MONTHLY = "MONTHLY",
    YEARLY = "YEARLY",
}