package org.jmorrey.examples;

import org.familysearch.api.client.ft.FamilySearchFamilyTree;
import org.familysearch.api.client.ft.FamilyTreePersonParentsState;
import org.familysearch.api.client.ft.FamilyTreePersonState;
import org.gedcomx.conclusion.Person;

import java.net.URI;
import java.util.List;

/**
 * Created by jmorrey on 5/10/15.
 * Very simple app demonstrating how to traverse a pedigree using FamilySearch's public API
 */
public class FsTreeTraversal {

  private FamilySearchFamilyTree tree;
  private String startId;
  private int generations;
  private FamilyTreePersonState startPerson;

  public FsTreeTraversal() throws Exception{
    ConsoleReader cr = new ConsoleReader();
    String user = cr.readLine("Enter your FamilySearch username: ");
    String password = new String(cr.readPassword("Enter your FamilySearch password: "));
    String devKey = cr.readLine("Enter your FamilySearch developer key: ");
    startId = cr.readLine("Enter a starting Person ID: ");
    generations = new Integer(cr.readLine("Enter the number of generations to traverse: "));
    tree = getTree(user, password, devKey);
    startPerson = tree.readPersonById(startId);
    if (startPerson == null || startPerson.getPerson() == null || startPerson.getPerson().getDisplayExtension() == null){
      throw new Exception("Failed to find person with ID " + startId);
    }
  }

  public static void main(String[] args) throws Exception {
    FsTreeTraversal fsTreeTraversal = new FsTreeTraversal();
    fsTreeTraversal.doTraverse();
  }

  public void doTraverse(){
    doTraverseRec(startPerson, 1);
  }

  /**
   * Recursive helper function that traverses up someones tree
   * @param personState the start point for the traversal
   * @param currentGen the number of the current generation ("1" if we're just starting)
   */
  private void doTraverseRec(FamilyTreePersonState personState, int currentGen) {

    Person person = personState.getPerson();
    System.out.println(person.getDisplayExtension().getName() + " (" + person.getDisplayExtension().getLifespan() + ")");

    if (currentGen >= generations) {
      return; //base case
    }

    FamilyTreePersonParentsState parentsState = personState.readParents();
    if (parentsState != null) {
      List<Person> parents = parentsState.getPersons();
      if (parents != null) {
        for (Person parent : parents) {
          FamilyTreePersonState parentState = tree.readPersonById(parent.getId());
          if (parentState != null) {
            doTraverseRec(parentState, currentGen + 1);
          }
        }
      }
    }
  }


  private FamilySearchFamilyTree getTree(String user, String password, String devKey) {
    URI collectionUri = URI.create("https://familysearch.org/platform/collections/tree");

    //read the collection.
    FamilySearchFamilyTree tree = new FamilySearchFamilyTree(collectionUri);

    //authenticate
    tree.authenticateViaOAuth2Password(user, password, devKey);
    return tree;
  }


}
