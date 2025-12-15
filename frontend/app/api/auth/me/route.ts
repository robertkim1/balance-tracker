import { NextRequest, NextResponse } from "next/server";

export async function GET(req: NextRequest) {
  const hasJwtCookie = Boolean(req.cookies.get("jwt")?.value);

  if (!hasJwtCookie) {
    return NextResponse.json(
      { isAuthenticated: false },
      { status: 401 }
    );
  }

  return NextResponse.json({ isAuthenticated: true });
}