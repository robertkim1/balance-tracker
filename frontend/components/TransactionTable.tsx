import { Transaction } from "@/types/transaction";

interface Props {
  transactions: Transaction[]
  onEdit: (tx: Transaction) => void
  onDelete: (id: string) => void
}

export default function TransactionTable({ transactions, onEdit, onDelete }: Props) {
  return (
    <table style={{ width: "100%", borderCollapse: "collapse" }}>
      <thead>
        <tr>
          <th>Source</th>
          <th>Amount</th>
          <th>Date</th>
          <th>Type</th>
          <th>Pay Period</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        {transactions.map(tx => (
          <tr key={tx.id}>
            <td>{tx.sourceName}</td>
            <td>{tx.amount}</td>
            <td>{tx.date}</td>
            <td>{tx.type}</td>
            <td>{tx.payPeriod}</td>
            <td>
              <button onClick={() => onEdit(tx)}>Edit</button>
              <button onClick={() => onDelete(tx.id)}>Delete</button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
