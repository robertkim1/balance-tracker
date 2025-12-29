import { TransactionEntity } from "./transaction";

export enum ProjectionTimeframe {
    ONE_YEAR = "ONE_YEAR",
    TWO_YEARS = "TWO_YEARS",
    FIVE_YEARS = "FIVE_YEARS",
}

export enum SummarizeDateBy {
    DAY = "DAY",
    MONTH = "MONTH",
    YEAR = "YEAR",
}

export type BalanceDataRequest = {
    transactions: TransactionEntity[];
    currBalance: number;
    summarizeDateBy: SummarizeDateBy;
    startDate: string;
    projectionTimeframe: ProjectionTimeframe;
}