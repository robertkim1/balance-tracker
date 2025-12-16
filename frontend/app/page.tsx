"use client";

import { useEffect, useState } from "react";
import { useAuth } from "../lib/auth-context";

type UserData = {
  username: string;
  email: string;
};

export default function Home() {
  const { isAuthenticated, isLoading, signInWithGoogle, signOut } = useAuth();
  const [userData, setUserData] = useState<UserData | null>(null);
  const [dataLoading, setDataLoading] = useState(false);
  const [dataError, setDataError] = useState<string | null>(null);

  useEffect(() => {
    if (!isAuthenticated) return;

    const getUserData = async () => {
      setDataLoading(true);
      setDataError(null);
      try {
        const userDataAPI = `${process.env.NEXT_PUBLIC_BACKEND_URL}/api/balance/userdata`;
        const res = await fetch(userDataAPI, { credentials: "include" });

        if (!res.ok) {
          throw new Error(`Failed to fetch user data: ${res.status}`);
        }

        const data = await res.json();
        setUserData(data);
      } catch (err: unknown) {
        if (err instanceof Error) {
          setDataError(err.message);
        } else {
          setDataError("Unknown error");
        }
      } finally {
        setDataLoading(false);
      }
    };

    getUserData();
  }, [isAuthenticated]);

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

        {dataLoading && <p>Loading user data...</p>}
        {dataError && <p className="text-red-500">{dataError}</p>}
        {userData && (
          <div className="space-y-2">
            <p>Username: {userData.username}</p>
            <p>Email: {userData.email}</p>
          </div>
        )}

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
