"use client";

import { useAuth } from "../lib/auth-context";
import { supabase } from "../lib/supabase";

export default function Home() {
  const { jwt } = useAuth();

  const signInWithGoogle = async () => {
    const { error } = await supabase.auth.signInWithOAuth({
      provider: "google",
      options: {
        redirectTo: process.env.BACKEND_API_URL_DEV + "/auth/callback"
      }
    });

    if (error) {
      console.log("Error:", error);
      return;
    }

    console.log("Redirecting…");
  };

  if (!jwt) {
    return (
      <main className="flex h-screen items-center justify-center">
        <button
          className="px-6 py-3 rounded-md bg-blue-600 text-white text-lg"
          onClick={signInWithGoogle}
        >
          Sign in with Google
        </button>
      </main>
    );
  }

  return (
    <main className="flex h-screen items-center justify-center">
      <p>Welcome! Your JWT is now loaded.</p>
    </main>
  );
}
