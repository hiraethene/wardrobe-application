package com.example.wardrobe_application;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.firestore.Exclude;

/**
 * <p>
 *     WardrobeItem class represents a single item in the user's wardrobe. It stores the item's attributes,
 *     including its image reference, category, title, description, brand, size, condition, colour,
 *     material and price. documentID is locally stored, and is excluded from firebase database storage as its used
 *     as an identifier for item deletion.
 * </p>
 *<p>
 *     WardrobeItem class implements Parcelable, which allows items to be passed between activities using intents.
 *</p>
 */
public class WardrobeItem implements Parcelable {
    // Item attributes

    // Excludes documentID from firebase database storage.
    @Exclude
    private String documentID;
    private String itemImage;
    private String itemCategory;
    private String itemTitle;
    private String itemDescription;
    private String itemBrand;
    private String itemSize;
    private String itemCondition;
    private String itemColour;
    private String itemMaterial;
    private String itemPrice;

    /**
     * This is an empty constructor as Firestore needs a constructor with no arguments.
     * This is required for Firestore deserialization.
     */
    public WardrobeItem() {}
    /**
     * <p>
     * This is a constructor that initializes the item object with parameters for the image, category,
     * title, description, brand, size, condition, colour, material and price.
     * </p>
     *
     * @param image        the image reference of the item.
     * @param category     the item's category.
     * @param title        the item's title.
     * @param description  the item's description.
     * @param brand        the item's brand.
     * @param size         the item's size.
     * @param condition    the item's condition.
     * @param colour       the item's colour.
     * @param material     the item's material.
     * @param price        the item's price.
     */
    public WardrobeItem(String image, String category, String title, String description, String brand, String size, String condition, String colour, String material, String price) {
        itemImage = image;
        itemCategory = category;
        itemTitle = title;
        itemDescription = description;
        itemBrand = brand;
        itemSize = size;
        itemCondition = condition;
        itemColour = colour;
        itemMaterial = material;
        itemPrice = price;
    }

    // Getters
    public String getDocumentID() {return documentID;}
    public String getItemImage() {return itemImage;}
    public String getItemCategory() {return itemCategory;}
    public String getItemTitle() {return itemTitle;}
    public String getItemDescription() {return itemDescription;}
    public String getItemBrand() {return itemBrand;}
    public String getItemSize() {return itemSize;}
    public String getItemCondition() {return itemCondition;}
    public String getItemColour() {return itemColour;}
    public String getItemMaterial() {return itemMaterial;}
    public String getItemPrice() {return itemPrice;}

    // Setter for the document ID of the item
    public void setDocumentID(String documentID) {this.documentID = documentID;}

    /**
     * <p>
     *     Constructs a WardrobeItem from a parcel which is used by the parcelable creator to create an item object.
     *     This constructor is private so only the 'CREATOR' field can access.
     * </p>
     * @param in a variable used to retrieve the values written into the parcel.
     */
    private WardrobeItem(Parcel in) {
        documentID = in.readString();
        itemImage = in.readString();
        itemCategory = in.readString();
        itemTitle = in.readString();
        itemDescription = in.readString();
        itemBrand = in.readString();
        itemSize = in.readString();
        itemCondition = in.readString();
        itemColour = in.readString();
        itemMaterial = in.readString();
        itemPrice = in.readString();

    }

    /**
     * Creates WardrobeItem instances from parcels and creates arrays of WardrobeItem item objects.
     */
    public static final Parcelable.Creator<WardrobeItem> CREATOR = new Creator<>() {
        // Calls the WardrobeItem constructor and
        // passes along the 'Parcel' and returns the new item object.
        @Override
        public WardrobeItem createFromParcel(Parcel in) {
            return new WardrobeItem(in);
        }

        @Override
        public WardrobeItem[] newArray(int size) {
            return new WardrobeItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Writes the WardrobeItem data values to save to the Parcel.
     *
     * @param dest The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     * May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(documentID);
        dest.writeString(itemImage);
        dest.writeString(itemCategory);
        dest.writeString(itemTitle);
        dest.writeString(itemDescription);
        dest.writeString(itemBrand);
        dest.writeString(itemSize);
        dest.writeString(itemCondition);
        dest.writeString(itemColour);
        dest.writeString(itemMaterial);
        dest.writeString(itemPrice);
    }
}
