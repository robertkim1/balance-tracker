import { NextRequest, NextResponse } from "next/server";

export async function POST(req: NextRequest) {
  const { token } = await req.json();
  if (!token) { 
    return NextResponse.json({ error: "missing token" }, { status: 400 });
  }

  const res = NextResponse.json({ ok: true });

  res.cookies.set({
    name: "jwt",
    value: token,
    httpOnly: true,
    secure: process.env.NODE_ENV === "production",
    sameSite: "lax",
    path: "/",
    maxAge: 60 * 60, // 1 hour
  });

  return res;
}
