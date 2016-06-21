package com.rebelo.messgame.entities.states;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.rebelo.messgame.entities.AnimalAgent;

/**
 * Created by sondu on 6/12/2016.
 */
public enum AnimalState implements State<AnimalAgent> {
    REST() {
        @Override
        public void enter(AnimalAgent entity) {

        }

        @Override
        public void update(AnimalAgent entity) {

        }

        @Override
        public void exit(AnimalAgent entity) {

        }

        @Override
        public boolean onMessage(AnimalAgent entity, Telegram telegram) {
            return false;
        }
    },
}
