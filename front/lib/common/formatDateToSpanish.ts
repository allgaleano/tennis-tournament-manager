export const formateDateToSpanish = (dateString: string) => {
  const date = new Date(dateString);
  return date.toLocaleString("es-ES", {
    year: "numeric",
    month: "long",
    day: "numeric",
    hour: "numeric",
    minute: "numeric",
  })
}