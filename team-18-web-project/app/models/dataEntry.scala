/**package models

import play.db.DB
import play.api.db
import play.api.Play
/**
  * Created by haesa on 6/16/2016.
  */
case class dataEntry(username: String, password: String, email: String, name: String) {
  object dataEntry {
    def all(): List[dataEntry] = Nil

    def create(username: String, password: String, email: String, name: String) {
      INSERT INTO users ( field1, field2,.
    }..fieldN )
      VALUES
      ( value1, value2,...valueN );
    def delete (username: String, password: String, email: String, name: String) {}
  }
}
*/