import { Button } from "@/components/ui/button";
import MatchResultsForm from "@/components/pages/tournaments/matches/sets/match-results-form";
import { Match } from "@/types";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Sheet, SheetContent, SheetHeader, SheetTitle, SheetTrigger } from "@/components/ui/sheet";

interface MatchResultsDialogProps {
  match: Match;
  tournamentId: number;
}

const MatchResultsSheet = ({ 
  match,
  tournamentId
} : MatchResultsDialogProps) => {
  return (
    <div className="h-20 grid items-center">
      <Sheet>
        <SheetTrigger asChild>
          <Button 
            variant="outline"
          >
            Anotar Resultados
          </Button>
        </SheetTrigger>
        <SheetContent side="right" className="bg-white">
          <SheetHeader>
            <SheetTitle className="mb-2">{match.player1?.surname} vs {match.player2?.surname}</SheetTitle>
          </SheetHeader>
          <ScrollArea className="h-[90vh]">
            <MatchResultsForm match={match} tournamentId={tournamentId}/>
          </ScrollArea>
        </SheetContent>
      </Sheet>
    </div>
  )
}

export default MatchResultsSheet;