/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.fluent.simulation;

import org.drools.fluent.FluentTest;
import org.drools.fluent.knowledge.KnowledgeBaseSimFluent;
import org.drools.fluent.knowledge.KnowledgeBuilderSimFluent;
import org.drools.fluent.session.StatefulKnowledgeSessionSimFluent;

public interface SimulationStepFluent extends FluentTest<SimulationStepFluent>  {

    SimulationStepFluent newStep(long distance);
    
    SimulationPathFluent end();

    KnowledgeBuilderSimFluent newKnowledgeBuilder();
    KnowledgeBaseSimFluent newKnowledgeBase();
    StatefulKnowledgeSessionSimFluent newStatefulKnowledgeSession();

    KnowledgeBuilderSimFluent getKnowledgeBuilder();
    KnowledgeBaseSimFluent getKnowledgeBase();
    StatefulKnowledgeSessionSimFluent getStatefulKnowledgeSession();

    KnowledgeBuilderSimFluent getKnowledgeBuilder(String name);
    KnowledgeBaseSimFluent getKnowledgeBase(String name);
    StatefulKnowledgeSessionSimFluent getStatefulKnowledgeSession(String name);

}
