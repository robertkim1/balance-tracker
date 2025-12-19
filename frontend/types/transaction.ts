
export type Transaction = {
    id: string;
    sourceName: string;
    amount: number;
    date: string;
    type: TransactionType; 
    payPeriod: PayPeriod;
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