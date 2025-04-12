package com.rebelo.messgame.entities.states;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.rebelo.messgame.entities.HumanAgent;

/**
 * 1. Biological and Physiological needs - air, food, drink, shelter, warmth, sex, sleep.

 2. Safety needs - protection from elements, security, order, law, stability, freedom from fear.

 3. Love and belongingness needs - friendship, intimacy, affection and love, - from work group, family, friends, romantic relationships.

 4. Esteem needs - achievement, mastery, independence, status, dominance, prestige, self-respect, respect from others.

 5. Self-Actualization needs - realizing personal potential, self-fulfillment, seeking personal growth and peak experiences.

 * Created by sondu on 6/12/2016.
 */
public enum HumanState implements State<HumanAgent> {
    PHYSIOLIGICAL() {
        @Override
        public void enter(HumanAgent entity) {

        }

        @Override
        public void update(HumanAgent entity) {

        }

        @Override
        public void exit(HumanAgent entity) {

        }

        @Override
        public boolean onMessage(HumanAgent entity, Telegram telegram) {
            return false;
        }
    },

    SAFETY() {
        @Override
        public void enter(HumanAgent entity) {

        }

        @Override
        public void update(HumanAgent entity) {

        }

        @Override
        public void exit(HumanAgent entity) {

        }

        @Override
        public boolean onMessage(HumanAgent entity, Telegram telegram) {
            return false;
        }
    },

    BELONGNESS() {
        @Override
        public void enter(HumanAgent entity) {

        }

        @Override
        public void update(HumanAgent entity) {

        }

        @Override
        public void exit(HumanAgent entity) {

        }

        @Override
        public boolean onMessage(HumanAgent entity, Telegram telegram) {
            return false;
        }
    },

    ESTEEM() {
        @Override
        public void enter(HumanAgent entity) {

        }

        @Override
        public void update(HumanAgent entity) {

        }

        @Override
        public void exit(HumanAgent entity) {

        }

        @Override
        public boolean onMessage(HumanAgent entity, Telegram telegram) {
            return false;
        }
    },

    ACTUALIZATION() {
        @Override
        public void enter(HumanAgent entity) {

        }

        @Override
        public void update(HumanAgent entity) {

        }

        @Override
        public void exit(HumanAgent entity) {

        }

        @Override
        public boolean onMessage(HumanAgent entity, Telegram telegram) {
            return false;
        }
    }
}
