"use client";

import { useEffect } from "react";
import { supabase } from "../../../lib/supabase";

export default function CallbackPage() {
  useEffect(() => {
    const run = async () => {
      const { data, error } = await supabase.auth.getSession();

      const token = data?.session?.access_token;

      if (token) {
        await fetch("/api/auth/set-cookie", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          credentials: "include",
          body: JSON.stringify({ token })
        });
      }

      window.location.replace("/");
    };

    run();
  }, []);

  return <p>Signing you in…</p>;
}
