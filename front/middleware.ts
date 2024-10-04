import { NextRequest, NextResponse } from "next/server";
import { authRoutes, DEFAULT_LOGIN_REDIRECT, publicRoutes } from "./routes";

export async function middleware(request: NextRequest) {
    const { nextUrl } = request;
    const userData = null;
    
    const isPublicRoute = publicRoutes.includes(nextUrl.pathname);
    const isAuthRoute = authRoutes.includes(nextUrl.pathname);

    if (isPublicRoute) {
        return;
    }

    if (isAuthRoute) {
        if (userData) {
            return NextResponse.redirect(new URL(DEFAULT_LOGIN_REDIRECT, nextUrl));
        }
        return;
    }

    if (!userData) {
        return NextResponse.redirect(new URL("/login", nextUrl));
    }

    return;
}

export const config = {
    matcher: ["/((?!.+\\.[\\w]+$|_next).*)", "/", "/(api|trpc)(.*)"],
};