"use client";

import { useEffect } from "react";
import { supabase } from "../../../lib/supabase";
import { useAuth } from "../../../lib/auth-context";
import { useRouter } from "next/navigation";

export default function CallbackPage() {
  const { setJwt } = useAuth();
  const router = useRouter();
  useEffect(() => {
    const processSession = async () => {
      const {
        data: { session }
      } = await supabase.auth.getSession();

      if (!session) {
        console.log("Session not found");
        return;
      }

      const jwt = session.access_token;
      console.log("Received Supabase JWT:", jwt);
      setJwt(jwt);
      router.replace("/");
    };

    processSession();
  }, [setJwt, router]);

  return (
    <main className="flex h-screen items-center justify-center">
      <p>Signing you in…</p>
    </main>
  );
}
