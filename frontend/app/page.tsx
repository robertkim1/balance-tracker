"use client";

import { useEffect, useState, useCallback } from "react";
import { useAuth } from "@/lib/auth/auth-context";

export default function Home() {
  const { isAuthenticated, isLoading, user, signInWithGoogle, signOut } = useAuth();

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
        <button
          className="px-6 py-3 rounded-md bg-blue-600 text-white text-lg hover:bg-blue-700 transition-colors"
          onClick={signInWithGoogle}
        >
          Sign in with Google
        </button>
      </main>
    );
  }

  return (
    <main className="flex h-screen items-center justify-center">
      <div className="text-center space-y-4">
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
      </div>
    </main>
  );
}
