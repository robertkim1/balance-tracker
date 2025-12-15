"use client";

import { useAuth } from "../lib/auth-context";

export default function Home() {
  const {
    isAuthenticated,
    isLoading,
    signInWithGoogle,
    signOut
  } = useAuth();

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
