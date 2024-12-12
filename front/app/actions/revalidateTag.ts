"use server";

import { revalidateTag as revalidate } from "next/cache";

export default async function revalidateTag(tag: string) {
  revalidate(tag);
}