"use client";

import { Button } from '@/components/ui/button';
import { Checkbox } from '@/components/ui/checkbox';
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import { useToast } from '@/hooks/use-toast';
import { getClientSideCookie } from '@/lib/users/getClientSideCookie';
import { matchScoreSchema } from '@/schemas';
import { Match } from '@/types'
import { zodResolver } from '@hookform/resolvers/zod';
import { MinusIcon, PlusIcon } from '@radix-ui/react-icons';
import { useRouter } from 'next/navigation';
import { useFieldArray, useForm } from 'react-hook-form';
import { z } from 'zod'

interface MatchResultsFormProps {
  match: Match;
  tournamentId: number;
}

const MatchResultsForm = ({ 
  match,
  tournamentId 
}: MatchResultsFormProps) => {
  const { toast } = useToast();
  const router = useRouter();

  const form = useForm<z.infer<typeof matchScoreSchema>>({
    resolver: zodResolver(matchScoreSchema),
    defaultValues: {
      sets: [
        {
          setNumber: 1,
          player1Games: 0,
          player2Games: 0,
          tiebreak: false,
          player1TiebreakGames: 0,
          player2TiebreakGames: 0
        }
      ]
    }
  });

  const { fields, append, remove } = useFieldArray({
    control: form.control,
    name: 'sets'
  });

  const onSubmit = async (data: z.infer<typeof matchScoreSchema>) => {
    // Transform the data to remove unnecessary tiebreak fields
    const formattedSets = data.sets.map(set => {
      const baseSet = {
        setNumber: set.setNumber,
        player1Games: set.player1Games,
        player2Games: set.player2Games
      };

      if (set.tiebreak) {
        return {
          ...baseSet,
          tiebreak: true,
          player1TiebreakGames: set.player1TiebreakGames,
          player2TiebreakGames: set.player2TiebreakGames
        };
      }

      return baseSet;
    });
    console.log(JSON.stringify({ sets : formattedSets }));
    try {
      const sessionId = getClientSideCookie("Session-Id") as string;
      if (!sessionId) {
        toast({
          variant: "destructive",
          title: "Necesitas iniciar sesión para guardar los resultados",
        });
      }

      const response = await fetch(`${process.env.NEXT_PUBLIC_API_URI}/tournaments/${tournamentId}/matches/${match.id}/sets`, {
        method: "POST",
        headers: {
          "Content-Type" : "application/json",
          "Session-Id": sessionId
        },
        body: JSON.stringify({ sets: formattedSets })
      });

      const data = await response.json();

      toast({
        variant: response.ok ? "success" : "destructive",
        title: data.title,
        ...(data.description && { description: data.description })
      });

      if (response.ok) {
        router.refresh();
      }
    } catch (error) {
      toast({
        variant: "destructive",
        title: "Ha ocurrido un error",
        description: "No se pudieron guardar los resultados del partido",
      });
    }
  };


  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)}>
        <div className="m-2 space-y-4 p-4">
          <div className="space-y-6">
            {fields.map((field, index) => (
              <div key={field.id} className="space-y-2 p-4 border rounded-sm">
                <div className="flex justify-between items-center">
                  <h2 className="font-semibold">Set {index + 1}</h2>
                  
                  <Button
                    type="button"
                    variant="ghost"
                    size="sm"
                    onClick={() => remove(fields.length - 1)}
                    disabled={fields.length <= 1}
                  >
                    <MinusIcon className="h-4 w-4" />
                  </Button>
                
                </div>
                <div className="flex flex-col gap-4">
                  <div className="grid grid-cols-2 gap-2">
                    <FormField
                      control={form.control}
                      name={`sets.${index}.player1Games`}
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>{match.player1?.name} {match.player1?.surname}</FormLabel>
                          <FormControl>
                            <Input type="number" min={0} max={7} {...field}
                              onChange={e => field.onChange(parseInt(e.target.value) || 0)}
                            />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                    <FormField
                      control={form.control}
                      name={`sets.${index}.player2Games`}
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel>{match.player2?.name} {match.player2?.surname}</FormLabel>
                          <FormControl>
                            <Input type="number" min={0} max={7} {...field}
                              onChange={e => field.onChange(parseInt(e.target.value) || 0)}
                            />
                          </FormControl>
                          <FormMessage />
                        </FormItem>
                      )}
                    />
                  </div>

                  <FormField
                    control={form.control}
                    name={`sets.${index}.tiebreak`}
                    render={({ field }) => (
                      <FormItem className="flex flex-row items-center space-x-2 space-y-0">
                        <FormControl>
                          <Checkbox
                            checked={field.value}
                            onCheckedChange={field.onChange}
                          />
                        </FormControl>
                        <FormLabel>Tiebreak</FormLabel>
                        <FormMessage />
                      </FormItem>
                    )}
                  />

                  {form.watch(`sets.${index}.tiebreak`) && (
                    <div className="grid grid-cols-2 gap-2">
                      <FormField
                        control={form.control}
                        name={`sets.${index}.player1TiebreakGames`}
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>{match.player1?.name} {match.player1?.surname}</FormLabel>
                            <FormControl>
                              <Input type="number" min={0} {...field}
                                onChange={e => field.onChange(parseInt(e.target.value) || 0)}
                              />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={form.control}
                        name={`sets.${index}.player2TiebreakGames`}
                        render={({ field }) => (
                          <FormItem>
                            <FormLabel>{match.player2?.name} {match.player2?.surname}</FormLabel>
                            <FormControl>
                              <Input type="number" min={0} {...field}
                                onChange={e => field.onChange(parseInt(e.target.value) || 0)}
                              />
                            </FormControl>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                    </div>
                  )}
                </div>
              </div>
            ))}
            <div className="flex justify-center items-center">
              <Button
                type="button"
                onClick={() => append({
                  setNumber: fields.length + 1,
                  player1Games: 0,
                  player2Games: 0,
                  tiebreak: false,
                  player1TiebreakGames: 0,
                  player2TiebreakGames: 0
                })}
                disabled={fields.length >= 5}
                className="flex items-center gap-2 font-semibold"
                variant="outline"
              >
                <PlusIcon className="h-4 w-4" />
                Añadir Set
              </Button>
            </div>
            <Button type="submit" className="w-full">Guardar</Button>
          </div>
        </div>
      </form>
    </Form>
  )
}

export default MatchResultsForm;