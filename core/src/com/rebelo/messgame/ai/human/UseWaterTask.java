/*******************************************************************************
 * Copyright 2015 See AUTHORS file.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.rebelo.messgame.ai.human;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.rebelo.messgame.entities.HumanAgent;

/**
 * Condition task that checks if the agent is hungry
 * 
 * @author davebaol
 */
public class UseWaterTask extends LeafTask<HumanAgent> {

	public UseWaterTask() {

	}

	// TODO: How do we know if the human has shelter?
	@Override
	public Status execute () {
		return Status.FAILED;
	}

	@Override
	protected Task<HumanAgent> copyTo (Task<HumanAgent> task) {
		return task;
	}

}
