"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useAuth } from "../../../lib/auth-context";

export default function CallbackPage() {
  const { isLoading } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (!isLoading) {
      // Auth state is handled by the context, just redirect
      router.replace("/");
    }
  }, [isLoading, router]);

  return (
    <main className="flex h-screen items-center justify-center">
      <p>Signing you in…</p>
    </main>
  );
}