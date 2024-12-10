"use client";

import { Button } from "@/components/ui/button";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Sheet, SheetContent, SheetHeader, SheetTitle, SheetTrigger } from "@/components/ui/sheet";
import { Match } from "@/types";
import { useRouter } from "next/navigation";
import React, { useState } from "react";

interface ChildProps {
  match: Match;
  tournamentId: number;
  onSuccess?: () => void;
}

interface ModalFormWrapperProps {
  match: Match;
  tournamentId: number;
  variant: "dialog" | "sheet";
  triggerText?: string;
  title?: string;
  children: React.ReactElement<ChildProps>;
  className?: string;
}

const ModalFormWrapper = ({
  match,
  tournamentId,
  variant,
  triggerText = "Anotar Resultados",
  title,
  children,
  className
}: ModalFormWrapperProps) => {
  const [open, setOpen] = useState(false);
  const router = useRouter();

  const handleSuccess = () => {
    setOpen(false);

    setTimeout(() => {
      router.refresh();
    }, 600);
  }

  const childWithProps = React.cloneElement(children, {
    match,
    tournamentId,
    onSuccess: handleSuccess,
  } as ChildProps);

  const modalTitle = title || `${match.player1?.surname} vs ${match.player2?.surname}`;

  return (
    <div className={className}>
      {variant === "dialog" ? (
        <Dialog open={open} onOpenChange={setOpen}>
          <DialogTrigger asChild>
            <Button variant="outline">{triggerText}</Button>
          </DialogTrigger>
          <DialogContent className="max-w-xl">
            <DialogHeader>
              <DialogTitle>{modalTitle}</DialogTitle>
            </DialogHeader>
            <ScrollArea className="max-h-[80vh]">
              {childWithProps}
            </ScrollArea>
          </DialogContent>
        </Dialog>
      ) : (
        <Sheet open={open} onOpenChange={setOpen}>
          <SheetTrigger asChild>
            <Button variant="outline">{triggerText}</Button>
          </SheetTrigger>
          <SheetContent side="right" className="bg-white">
            <SheetHeader>
              <SheetTitle>{modalTitle}</SheetTitle>
            </SheetHeader>
            <ScrollArea className="h-[90vh]">
              {childWithProps}
            </ScrollArea>
          </SheetContent>
        </Sheet>
      )}
    </div>
  );
}

export default ModalFormWrapper;