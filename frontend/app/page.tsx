"use client";

import { useState, useCallback } from "react";
import { useAuth } from "@/lib/auth/auth-context";
import { Transaction } from "@/types/transaction";
import { Button } from "@/components/ui/button";
import TransactionTable from "@/components/TransactionTable";
import TransactionModal from "@/components/TransactionModal";
import {
  useQuery,
  useMutation,
  useQueryClient,
} from "@tanstack/react-query";

export default function Home() {
  const { isAuthenticated, isLoading, signInWithGoogle, signOut } = useAuth();
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState<Transaction | null>(null);
  const queryClient = useQueryClient();

  const getBackendToken = useCallback(async () => {
    const res = await fetch("/api/auth/token", { credentials: "include" });
    if (!res.ok) throw new Error("Failed to get backend JWT");
    const { token } = await res.json();
    return token;
  }, []);

  const fetchTransactions = async (): Promise<Transaction[]> => {
    const token = await getBackendToken();
    const res = await fetch(
      `${process.env.NEXT_PUBLIC_BACKEND_URL}/api/transactions`,
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
    mutationFn: async (tx: Transaction) => {
      const token = await getBackendToken();
      const method = tx.id ? "PUT" : "POST";
      const url = tx.id
        ? `${process.env.NEXT_PUBLIC_BACKEND_URL}/api/transactions/${tx.id}`
        : `${process.env.NEXT_PUBLIC_BACKEND_URL}/api/transactions`;

      const res = await fetch(url, {
        method,
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(tx),
      });

      if (!res.ok) throw new Error("Save failed");
      return res.json();
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
        `${process.env.NEXT_PUBLIC_BACKEND_URL}/api/transactions/${id}`,
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

  function submitAll() {
    fetch("/api/submit", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(transactions),
    });
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
          <Button onClick={() => setOpen(true)}>Add Transaction</Button>
          <Button variant="secondary" onClick={submitAll}>
            Submit All
          </Button>
          <Button variant="secondary" onClick={signOut}>
            Sign Out
          </Button>
        </div>

        {isFetching && <p className="text-sm">Refreshing…</p>}

        <TransactionTable
          transactions={transactions}
          onEdit={tx => {
            setEditing(tx);
            setOpen(true);
          }}
          onDelete={id => deleteMutation.mutate(id)}
        />

        <TransactionModal
          key={editing?.id ?? "new"}
          open={open}
          onOpenChange={setOpen}
          initialData={editing}
          onSave={tx => saveMutation.mutate(tx)}
        />
      </div>
    </main>
  );
}
