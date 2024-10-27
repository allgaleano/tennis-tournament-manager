export const formateDateToSpanish = (dateString: string) => {
  const date = new Date(dateString);
  return date.toLocaleString("es-Es", {
    year: "numeric",
    month: "long",
    day: "numeric",
    hour: "numeric",
    minute: "numeric",
  })
}