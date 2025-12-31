"use client";

import { useState, useCallback } from "react";
import { useAuth } from "@/lib/auth/auth-context";
import { TransactionInput, TransactionEntity } from "@/types/transaction";
import { Button } from "@/components/ui/button";
import { Select, SelectTrigger, SelectContent, SelectValue, SelectItem } from "@/components/ui/select";
import TransactionTable from "@/components/TransactionTable";
import TransactionModal from "@/components/TransactionModal";
import BalanceChart from "@/components/BalanceChart";
import {
  useQuery,
  useMutation,
  useQueryClient,
} from "@tanstack/react-query";
import { BalanceDataRequest, ProjectionDataItem, ProjectionTimeframe, SummarizeDateBy } from "@/types/dashboard";
import { Input } from "@/components/ui/input";

export default function Home() {
  const { isAuthenticated, isLoading, signInWithGoogle, signOut } = useAuth();
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState<TransactionEntity | null>(null);
  const [modalKeyCounter, setModalKeyCounter] = useState(0);
  const [projectionTimeframe, setProjectionTimeframe] = useState<ProjectionTimeframe>(ProjectionTimeframe.ONE_YEAR);
  const [summarizeDateBy, setSummarizeDateBy] = useState<SummarizeDateBy>(SummarizeDateBy.DAY);
  const [startDate, setStartDate] = useState("");
  const [currBalance, setCurrBalance] = useState(0);
  const [projectionData, setProjectionData] = useState<ProjectionDataItem[]>([]);
  const queryClient = useQueryClient();

  function openModal(tx?: TransactionEntity) {
    setEditing(tx ?? null);
    if (!tx) setModalKeyCounter(prev => prev + 1);
    setOpen(true);
  }

  const getBackendToken = useCallback(async () => {
    const res = await fetch("/api/auth/token", { credentials: "include" });
    if (!res.ok) throw new Error("Failed to get backend JWT");
    const { token } = await res.json();
    return token;
  }, []);

  const fetchTransactions = async (): Promise<TransactionEntity[]> => {
    const token = await getBackendToken();
    const res = await fetch(
      `${process.env.NEXT_PUBLIC_BACKEND_URL}/api/balance/transactions`,
      {
        headers: { Authorization: `Bearer ${token}` },
      }
    );
    if (!res.ok) throw new Error("Failed to load transactions");
    return res.json();
  };

  const { data: transactions = [], isFetching } = useQuery({
    queryKey: ["transactions"],
    queryFn: fetchTransactions,
    enabled: isAuthenticated,
    staleTime: 60_000,
  });

  const saveMutation = useMutation({
    mutationFn: async (input: TransactionInput) => {
      const token = await getBackendToken();

      const isEdit = Boolean(editing?.id);
      const url = isEdit
        ? `${process.env.NEXT_PUBLIC_BACKEND_URL}/api/balance/transactions/${editing!.id}`
        : `${process.env.NEXT_PUBLIC_BACKEND_URL}/api/balance/transactions`;

      const method = isEdit ? "PUT" : "POST";

      const res = await fetch(url, {
        method,
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(input),
      });

      if (!res.ok) throw new Error("Save failed");

      return res.json() as Promise<TransactionEntity>;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["transactions"] });
      setEditing(null);
      setOpen(false);
    },
  });

  const deleteMutation = useMutation({
    mutationFn: async (id: string) => {
      const token = await getBackendToken();
      const res = await fetch(
        `${process.env.NEXT_PUBLIC_BACKEND_URL}/api/balance/transactions/${id}`,
        {
          method: "DELETE",
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      if (!res.ok) throw new Error("Delete failed");
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["transactions"] });
    },
  });

  async function submitAll() {
    const request: BalanceDataRequest = {
      transactions,
      currBalance,
      summarizeDateBy,
      projectionTimeframe,
      startDate,
    };
    const token = await getBackendToken();
    const res = await fetch(`${process.env.NEXT_PUBLIC_BACKEND_URL}/api/balance/submit`, {
      method: "POST",
      headers: { Authorization: `Bearer ${token}`,
        "Content-Type": "application/json" },
      body: JSON.stringify(request),
    });
    const data = await res.json();
    setProjectionData(data);
  }

  if (isLoading) {
    return (
      <main className="flex h-screen items-center justify-center">
        <p>Loading...</p>
      </main>
    );
  }

  if (!isAuthenticated) {
    return (
      <main className="flex h-screen items-center justify-center">
        <Button onClick={signInWithGoogle}>Sign in with Google</Button>
      </main>
    );
  }

  return (
    <main className="flex justify-center mt-12">
      <div className="w-[70%] space-y-4">
        <div className="flex gap-2">
          <Button onClick={() => openModal()}>Add Transaction</Button>
          <Button variant="secondary" onClick={submitAll}>
            Submit All
          </Button>
          <Input
            placeholder="Current Balance"
            value={currBalance}
            onChange={(e) => setCurrBalance(Number(e.target.value))}
          />
          <Select
            value={projectionTimeframe}
            onValueChange={(v) => setProjectionTimeframe(v as ProjectionTimeframe)}
          >
            <SelectTrigger>
              <SelectValue placeholder="Projection Timeframe" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value={ProjectionTimeframe.ONE_YEAR}>One Year</SelectItem>
              <SelectItem value={ProjectionTimeframe.TWO_YEARS}>Two Years</SelectItem>
              <SelectItem value={ProjectionTimeframe.FIVE_YEARS}>Five Years</SelectItem>
            </SelectContent>
          </Select>
          <Select
            value={summarizeDateBy}
            onValueChange={(v) => setSummarizeDateBy(v as SummarizeDateBy)}
          >
            <SelectTrigger>
              <SelectValue placeholder="Summarize Date By" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value={SummarizeDateBy.DAY}>Day</SelectItem>
              <SelectItem value={SummarizeDateBy.MONTH}>Month</SelectItem>
              <SelectItem value={SummarizeDateBy.YEAR}>Year</SelectItem>
            </SelectContent>
          </Select>
          <Input
            type="date"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
          />
          <Button variant="secondary" onClick={signOut}>
            Sign Out
          </Button>
        </div>

        {isFetching && <p className="text-sm">Refreshing…</p>}

        <TransactionTable
          transactions={transactions}
          onEdit={tx => 
            openModal(tx)
          }
          onDelete={id => deleteMutation.mutate(id)}
        />

        {projectionData.length > 0 && (
          <div className="mt-6">
            <BalanceChart projectionData={projectionData} />
          </div>
        )}

        <TransactionModal
          key={editing?.id ?? `new-${modalKeyCounter}`}
          open={open}
          onOpenChange={setOpen}
          initialData={editing}
          onSave={tx => saveMutation.mutate(tx)}
        />
      </div>
    </main>
  );
}
