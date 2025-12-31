"use client";

import { Line } from "react-chartjs-2";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  ChartOptions,
  TooltipItem
} from "chart.js";
import { ProjectionDataItem } from "@/types/dashboard";
import { TransactionEntity } from "@/types/transaction";

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
);

interface BalanceChartProps {
  projectionData: ProjectionDataItem[];
}

export default function BalanceChart({ projectionData }: BalanceChartProps) {
  const chartLabels = projectionData.map(d => d.date);
  const chartBalances = projectionData.map(d => d.dataPoint.balance);

  const chartData = {
    labels: chartLabels,
    datasets: [
      {
        label: "Balance Over Time",
        data: chartBalances,
        borderColor: "rgb(75, 192, 192)",
        backgroundColor: "rgba(75, 192, 192, 0.2)",
        tension: 0.3,
        pointRadius: 5
      },
    ],
  };

  const chartOptions: ChartOptions<"line"> = {
    responsive: true,
    plugins: {
      legend: { position: "top" },
      title: { display: true, text: "Projected Balance" },
      tooltip: {
        callbacks: {
          // Customize the tooltip label
          label: function (tooltipItem: TooltipItem<"line">) {
            const idx = tooltipItem.dataIndex ?? 0;
            const item = projectionData[idx];
            const balance = item.dataPoint.balance;
            const transactions: TransactionEntity[] = item.dataPoint.transactionList;

            // Create a string showing the balance + transactions
            let tooltipText = `Balance: $${balance}`;
            if (transactions.length > 0) {
              tooltipText += "\nTransactions:";
              transactions.forEach(tx => {
                tooltipText += `\n- ${tx.sourceName}: $${tx.amount} (${tx.type})`;
              });
            }
            return tooltipText;
          }
        }
      }
    }
  };

  return (
    <div>
      <Line data={chartData} options={chartOptions} />
    </div>
  );
}
