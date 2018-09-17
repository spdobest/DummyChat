/*
 * Copyright © 2016, Craftsvilla.com
 * Written under contract by Robosoft Technologies Pvt. Ltd.
 */

package android.chat.util;

/**
 * Created by Sibaprasad on 20/10/16.
 */
public class Constants {
	public static final String SPACE = " ";
	public static final String EMPTY_TEXT = "";
	public static final String COMMA = ", ";
	public static final String BRACKET_OPEN = "(";
	public static final String BRACKET_CLOSE = ")";
	public static final String PLUS = " + ";
	public static final String HYPHEN = " - ";
	public static final String SLASH = "/-";
	public static final String VERTICAL_SCORE = " || ";
	public static final String PERCENTAGE = "%";
	public static final String COLON = " : ";
	public static final String SELECT_ALL = "Select All";

	public static final  int PICK_PDF_CODE = 2342;

	public static final short 	TAB_CONTACTS  	= 		1;
	public static final short   TAB_GROUP	 	=		2;

	public static final String   TAB_NAME_CONTACT     	=		"Contacts";
	public static final String   TAB_NAME_CHAT 			=		"Chat";

	public static final String FIREBASE_URL1 = /*"https://MyBasket.firebaseio.com/";*/ "https://mybasket.firebaseIO.com";
	public static final String FIREBASE_URL =  "https://my-basket-50396.firebaseio.com/";
	// constants for screens
	public static final int FFROM_CHECKOUTSCREEN = 1;
	public static final int FFROM_OTHER_SCREEN = 2;

	/**
	 * ALL SUBJECTS
	 */
	public static final String SUBJECT_ANDROID = "Android";
	public static final String SUBJECT_IPHONE = "Iphone";
	public static final String SUBJECT_JAVA = "Java";
	public static final String SUBJECT_PYTHON = "Python";

	public interface HeaderKeys {
		String API_VERSION_CODE = "X-VERSION-CODE";
		String X_CLIENT = "X-Client";
		String CONTENT_TYPE = "Content-Type";
		String CACHE_CONTROL = "Cache-Control";
		String AUTHORIZATION = "Authorization";
		String X_SESSION = "X-Session";
	}

	public interface AppConstants {
		String PLATFORM = "Android";
		String CONTENT_FORMAT = "application/json";
		String NO_CACHE = "no-cache";
	}

	public interface BundleKeys {
		String FROM_WHICH_SCREEN = "from_which_screen";
		String LOGIN_SCREEN = "login_screen";
		String IMAGE_TYPE = "imagetype";
		String productName = "ProductName";
		String IS_UPLOADING = "is_uploading";
		String FILE_PATH = "file_path";
		String FILE_TYPE = "file_type";
		String FRAGMENT_TITLE = "fragment_title";
		String USER_NAME = "username";
		String USER_ID = "user_id";
		String EMAIL = "email";
		String TAB_TYPE = "tabType";

		String LIST_IMAGE = "list_images";
		String POSITION = "position";
		String IS_SUBJECT_CHAT = "isSubject";
		String SUBJECT_CHAT = "Subject";
		String RECIEVER_NAME = "recieverName";
		String RECIEVER_ID = "recieverId";
		String GROUP_NAME = "groupName";
		String MESSAGE_FROM_NOTIFICATION = "messageFromNotification";
		String SENDER_RECIEVER_ID = "senderRecieverId";
	}

	public interface OrderStatus {
		String CURRENT = "CURRENT";
		String COMPLETED = "COMPLETED";
		String CANCELED = "CANCELLED";
	}

	public interface HomeContent {
		String SLIDER = "slider";
		String CATEGORY = "categoryBox";
		String PRODUCT_GRID = "productGrid";
		String VENDOR_GRID = "vendorShopGrid";
		String PAGE = "staticPage";
		String COLOR_GRID = "colorBox";
		String FEED_BOX = "feedBox";
	}

	public interface LoginSourceType {
		String FACEBOOK = "facebook";
		String GOOGLE = "google";
	}

	public interface ActivityRequestCodes {
		int GOOGLE_SIGN_IN = 555;
		int LOGIN_NAVIGATION = 1000;
		int LOGIN_COMPULSORY = 1001;
		int FILTER = 1002;
		int OAUTH_MANAGER = 1003;
		int FROM_CART_PAGE = 1004;
		int FROM_CART_PAGE_LOGIN = 1005;
		int PRODUCT_DETAIL_ACTIVITY = 1006;
	}

	public interface RequestTags {
		String AUTO_SUGGESTION = "auto_suggestion";
	}

	public interface FilterViewType {
		String COLOR = "colorGridView";
		String NESTED_LISTVIEW = "nestedMultiSelectListView";
		String MULTI_SELECT_VIEW = "multiSelectListView";
		String RADIO_VIEW = "radioListView";
	}

	public interface FilterType {
		String ATTRIBUTE_FILTER = "attributeFilter";
		String SUB_CAT_FILTER = "subCatFilter";

	}

	public interface Dialogs {
		String SPLASH = "splash";
		String SEARCH = "Search";
	}

	public interface BannerType {
		String FEED = "feed";
		String CATEGORY = "category";
		String PRODUCT = "product";
		String TAG = "tag";
	}

	public interface CodHeaderType {
		int NO_HEADER = 0;
		int COD_AVAILABLE = 1;
		int COD_NOT_AVAILABLE = 2;
	}

	public interface BroadCastReceiverType {
		String LOGOUT = "logout";
		String LOGOUT_SUCCESS = "logout_success";
	}

	public interface CheckPinState {
		int NONE = 0;
		int PIN_CAPTURED = 1;
		int COD_DETAILS_FETCHED = 2;
	}

	public interface FragmentNames {
		String PRODUCT_DETAIL = "productDetailsPage";
		String HOME_SCREEN = "homeScreen";
		String CATEGORY_PAGE = "categoryPage";
		String SEARCH_PAGE = "searchPage";
		String CART_PAGE = "cartPage";
		String ORDER_PAGE = "orderPage";
	}

	public interface Navigation {
		int MEN = 1;
		int WOMEN = 2;
		int KID = 3;
		int ELECTRONICS = 4;
		int ACCESSORIES = 5;
		int APPLIENCES = 6;

		int MY_ACCOUNT = 7;
		int MY_ORDER = 8;
		int MY_WISHLIST = 9;
		int MY_CART = 10;
		int MY_COLLECTION = 11;
		int OTHER = 13;
	}
	public interface FirebaseConstants{
		public static final String TABLE_CHAT 		= 	"ChatTable";
		public static final String TABLE_STUDENT 	= 	"StudentTable";
		public static final String TABLE_TEACHER 	= 	"TeacherTable";
		// SUBJECT
		public static final String TABLE_ANDROID 	= 	"Android";
		public static final String TABLE_IPHONE 	= 	"Iphone";
		public static final String TABLE_JAVA 		= 	"Java";
		public static final String TABLE_PYTHON 	= 	"Python";

		public static final String FIREBASE_IMAGESTORAGE = "gs://my-chat-f5ef1.appspot.com";

		public static final String STORAGE_PATH_UPLOADS = "uploads/";
		public static final String DATABASE_PATH_UPLOADS = "uploads";


		String STUDENT_TABLE        = "Student-";
		String TEACHER_TABLE        = "Teacher";
		String TABLE_USER            = "user_table";
		String ATTENDANCE_TABLE     = "Attendance-";
		String COURSE_TABLE         = "Course";
		String FIRSTYEAR            = "1stYear";
		String SECOND_YEAR          = "2ndYear";
		String THIRD_YEAR           = "3rdYear";
		String FOURTH_YEAR          = "4thYear";

		// key constants in database
		String STUDENT_ID              = "studentId";
		String EMAILID                 = "emailId";
		String NAME                    = "name";
		String MOBILENUMBER            = "mobileNumber";
		String PASSWORD                = "password";
		String SUBJECT                 = "subject";
		String TEACHER_ID              = "teacherId";
		String USER_ID                 = "userid";
		String SUBJECT_NAME            = "subjectName";
	}
}
