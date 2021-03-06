/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * Storage management
 *
 * <p>
 * All Items (Concept within Memory, TaskLinks and TermLinks within Concept, and Tasks within buffer)
 * are put into Bags, which supports priority-based resources allocation.
 * Also, bag supports access by key (String).
 * </p>
 *
 * <p>
 * A bag supports three major operations:
 * </p>
 *
 * <ul>
 * <li>To take out an item by key.</li>
 * <li>To take out an item probabilistically according to priority.</li>
 * <li>To put an item into the bag.</li>
 * </ul>
 *
 * <p>
 * All the operations take constant time to finish.
 * </p>
 *
 * <p>
 * The "take out by priority" operation takes an item out probablistically, with the
 * probability proportional to the priority value.
 * </p>
 *
 * <p>
 * The probability distribution is generated from a deterministic table.
 * </p>
 *
 * <p>
 * All classes in package <tt>org.opennars.storage</tt> extend <tt>Bag</tt>.
 * </p>
 *
 * <p>
 * In NARS, the memory consists of a bag of concepts.  Each concept uniquely corresponds to a term,
 * which uniquely corresponds to a String served as its name.  It is necessary to separate a term and
 * the corresponding concept, because a concept may be deleted due to space competition, and a term is
 * removed only when no other term is linked to it.  In the system, there may be multiple terms refer to
 * the same concept, though the concept just refer to one of them.  NARS does not follow a
 * "one term, one concept" policy and use a hash table in memory to maps names into terms, because the
 * system needs to remove a concept without removing the term that naming it.
 * </p>
 *
 * <p>
 * Variable terms correspond to no concept, and their meaning is local to the "smallest" term that
 * contains all occurences of the variable.
 * </p>
 *
 * <p>
 * From name to term, call Term.nameToTerm(String).  From name to concept, call Concept.nameToConcept(String).
 * Both use the name as key to get the concept from the concept hashtable in memory.
 * </p>
 *
 * <p>
 * The main memory also contains buffers for new tasks. One buffer contains tasks to be processed immediately
 * (to be finished in constant time), and the other, a bag, for the tasks to be processed later.
 * </p>
 */
package org.opennars.storage;
