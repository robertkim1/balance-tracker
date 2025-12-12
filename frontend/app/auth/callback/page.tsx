"use client";
import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { supabase } from "../../../lib/supabase";

export default function CallbackPage() {
  const router = useRouter();

  useEffect(() => {
    const handleAuth = async () => {
      const { data: { session } } = await supabase.auth.getSession();
      if (session?.access_token) {
        // Send token to Next.js API to set HttpOnly cookie
        await fetch("/api/auth/set-cookie", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ token: session.access_token })
        });
      }
      router.replace("/"); // redirect to home
    };

    handleAuth();
  }, [router]);

  return <p>Signing you in…</p>;
}
