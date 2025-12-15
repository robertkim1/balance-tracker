import { NextRequest, NextResponse } from "next/server";

export async function POST(req: NextRequest) {
  const jwt = req.cookies.get("jwt")?.value;

  if (jwt) {
    await fetch(`${process.env.NEXT_PUBLIC_SUPABASE_URL}/auth/v1/logout`, {
      method: "POST",
      headers: {
        apikey: process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY!,
        Authorization: `Bearer ${jwt}`
      }
    });
  }

  const res = NextResponse.json({ ok: true });

  res.cookies.set({
    name: "jwt",
    value: "",
    httpOnly: true,
    secure: process.env.NODE_ENV === "production",
    sameSite: "lax",
    path: "/",
    maxAge: 0
  });

  return res;
}
