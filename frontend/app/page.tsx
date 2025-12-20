"use client";

import { useEffect, useState, useCallback } from "react";
import { useAuth } from "@/lib/auth/auth-context";
import { Transaction } from "@/types/transaction";
import { Button } from "@/components/ui/button";
import TransactionTable from "@/components/TransactionTable";
import TransactionModal from "@/components/TransactionModal";

export default function Home() {
  const { isAuthenticated, isLoading, user, signInWithGoogle, signOut } = useAuth();
  const [transactions, setTransactions] = useState<Transaction[]>([])
  const [open, setOpen] = useState(false)
  const [editing, setEditing] = useState<Transaction | null>(null)

  function save(tx: Transaction) {
    setTransactions(prev => {
      const idx = prev.findIndex(t => t.id === tx.id)
      if (idx !== -1) {
        const copy = [...prev]
        copy[idx] = tx
        return copy
      }
      return [...prev, tx]
    })
    setEditing(null)
    setOpen(false)
  }

  function submitAll() {
    fetch("/api/submit", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(transactions)
    })
  }

  // Fetch a fresh backend JWT from Next.js
  const getBackendToken = useCallback(async () => {
    const tokenRes = await fetch("/api/auth/token", { credentials: "include" });
    if (!tokenRes.ok) throw new Error("Failed to get backend JWT");
    const { token } = await tokenRes.json();
    return token;
  }, []);

  // Fetch user data from Spring backend using backend JWT
  // technically don't need to this to fetch data
  // since the user stuff comes from our current session object
  // replace this with an actual call
  // const fetchUserData = useCallback(async () => {
  //   setDataLoading(true);
  //   setDataError(null);

  //   try {
  //     const token = await getBackendToken();
  //     const res = await fetch(
  //       `${process.env.NEXT_PUBLIC_BACKEND_URL}/api/balance/userdata`,
  //       {
  //         headers: {
  //           Authorization: `Bearer ${token}`,
  //         },
  //       }
  //     );

  //     if (!res.ok) throw new Error(`Failed to fetch user data: ${res.status}`);
  //     const data = await res.json();
  //     setUserData(data);
  //   } catch (err: unknown) {
  //     if (err instanceof Error) setDataError(err.message);
  //     else setDataError("Unknown error");
  //     setUserData(null);
  //   } finally {
  //     setDataLoading(false);
  //   }
  // }, [getBackendToken]);

  // Trigger fetching whenever the user is authenticated
  // useEffect(() => {
  //   if (!isAuthenticated) return;
  //   fetchUserData();
  // }, [isAuthenticated, fetchUserData]);

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
        <Button onClick={signInWithGoogle}>
            Sign in with Google
          </Button>
      </main>
    );
  }

  return (
    <main className="flex justify-center mt-12">
      {/* <div className="text-center space-y-4">
        <p className="text-lg">Welcome! You are signed in.</p>
        <div className="space-y-2">
          <p>Better Auth User Id: {user?.id}</p>
          <p>Email: {user?.email}</p>
        </div>
        <button
          className="px-6 py-3 rounded-md bg-blue-600 text-white text-lg hover:bg-blue-700 transition-colors"
          onClick={signOut}
        >
          Sign Out
        </button>
      </div> */}
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

        <TransactionTable
          transactions={transactions}
          onEdit={tx => {
            setEditing(tx)
            setOpen(true)
          }}
          onDelete={id =>
            setTransactions(prev => prev.filter(t => t.id !== id))
          }
        />

        <TransactionModal
          open={open}
          onOpenChange={setOpen}
          initialData={editing}
          onSave={save}
        />
      </div>
    </main>
  )
}
