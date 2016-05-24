package question2;

/**
   An item with a key and a value.
*/
public class Item implements Comparable<Item>
{
   private String key;
   private String value;

   /**
      Constructs an Item object.
      @param k the key string
      @param v the value of the item
   */
   public Item(String k, String v)
   { 
      key = k;
      value = v;
   }
   
   /**
      Gets the key.
      @return the key
   */
   public String getKey()
   { 
      return key;
   }
   
   /**
      Gets the value.
      @return the value
   */
   public String getValue()
   { 
      return value;
   }

   public int compareTo(Item otherObject)
   {
      Item other = (Item) otherObject;
      return key.compareTo(other.key);
   }
}
