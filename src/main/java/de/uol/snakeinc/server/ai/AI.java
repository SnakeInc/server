package de.uol.snakeinc.server.ai;

import de.uol.snakeinc.server.game.Action;
import de.uol.snakeinc.server.game.Game;
import de.uol.snakeinc.server.interactor.Interactor;

public class AI extends Interactor {

    private int turnCount = 1;

    public AI(String name, Game game) {
        super(name, game);
    }

    public void nextTurn() throws CloneNotSupportedException {
        if(active && !ready) {
            AIPosition aiPosition = new AIPosition(positionX, positionY, speed, direction.clone(), game.getMap().getMap(), turnCount);
            int[] result = new int[5];
            for (int i = 0; i < 5; i++) {
                aiPosition.init();
                result[i] = 0;
                if(aiPosition.calculateNewPosition(getAction(i))) {
                    for (int j = 0; j < 5; j++) {
                        if(aiPosition.calculateNewPosition(getAction(j))) {
                            for (int v = 0; v < 5; v++) {
                                if(aiPosition.calculateNewPosition(getAction(v))) {
                                    for (int n = 0; n < 5; n++) {
                                        if(aiPosition.calculateNewPosition(getAction(n))) {
                                            for (int m = 0; m < 5; m++) {
                                                if(aiPosition.calculateNewPosition(getAction(m))) {
                                                    for (int l = 0; l < 5; l++) {
                                                        if(aiPosition.calculateNewPosition(getAction(l))) {
                                                            for (int k = 0; k < 5; k++) {
                                                                if(aiPosition.calculateNewPosition(getAction(k))) {
                                                                    for (int h = 0; h < 5; h++) {
                                                                        if(aiPosition.calculateNewPosition(getAction(h))) {
                                                                            result[i]++;
                                                                        }
                                                                        aiPosition.resetToDepth(7);
                                                                    }
                                                                    result[i] = result[i] + 5;
                                                                }
                                                                aiPosition.resetToDepth(6);
                                                            }
                                                            result[i] = result[i] + 25;
                                                        }
                                                        aiPosition.resetToDepth(5);
                                                    }
                                                    result[i] = result[i] + 125;
                                                }
                                                aiPosition.resetToDepth(4);
                                            }
                                            result[i] = result[i] + 625;
                                        }
                                        aiPosition.resetToDepth(3);
                                    }
                                    result[i] = result[i] + 3125;
                                }
                                aiPosition.resetToDepth(2);
                            }
                            result[i] = result[i] + 15625;
                        }
                        aiPosition.resetToDepth(1);
                    }
                    result[i] = result[i] + 78125;
                }
            }
            int bestAction = 0;
            int max = 0;
            for (int k = 0; k < result.length; k++) {
                if (result[k] >= max) {
                    bestAction = k;
                    max = result[k];
                }
            }
            doAction(getAction(bestAction));

            game.gameReadyNextTurn();
            turnCount++;
        }
    }

    private Action getAction(int a) {
        if(a == 0) {
            return Action.CHANGE_NOTHING;
        } else if (a == 1) {
            return Action.TURN_LEFT;
        } else if (a == 2) {
            return Action.TURN_RIGHT;
        } else if (a == 3) {
            return Action.SPEED_UP;
        } else if (a == 4) {
            return Action.SLOW_DOWN;
        } else {
            return Action.CHANGE_NOTHING;
        }
    }

    private void doAction(Action action) {
        if(action.equals(Action.SLOW_DOWN)) {
            speedDown();
        } else if (action.equals(Action.TURN_LEFT)) {
            turnLeft();
        } else if (action.equals(Action.TURN_RIGHT)) {
            turnRight();
        } else if (action.equals(Action.SPEED_UP)) {
            speedUp();
        } else {
            doNothing();
        }
    }

    @Override
    public void died(String reason) {
        if(active) {
            active = false;
            ready = false;
            LOG.fine("Game: " + game.getGameId()+ " AI " + name + " died! Because: " + reason);
        }
    }
}
