package com.digitalminds.projectlibrary;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.digitalminds.projectlibrary.adapters.BooksCardAdapter;
import com.digitalminds.projectlibrary.models.Book;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BooksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BooksFragment extends Fragment {

    RecyclerView mBooksRecyclerView;
    BooksCardAdapter mBooksAdapter;
    Book[] books;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String category_key = "category";

    private String mCategory;

    public BooksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param category Parameter 1
     * @return A new instance of fragment BooksFragment.
     */
    public static BooksFragment newInstance(String category) {
        BooksFragment fragment = new BooksFragment();
        Bundle args = new Bundle();
        args.putString(category_key, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //todo here we're gonna call the real data from cloud
        //todo since the oncreate() method is called before the onCreateView() method
        if (getArguments() != null) {
            mCategory = getArguments().getString(category_key);
        }
        String fragTitle = mCategory;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //initialize view parent
        View view = inflater.inflate(R.layout.fragment_books, container, false);
        //displaying  books in books fragment
        books = new Book[]{
                new Book("title 1", "saad haj"),
                new Book("title 2", "mohammad abd"),
                new Book("title 3", "khaled othman"),
                new Book("title 4", "nana alhaj"),
                new Book("title 5", "abdulla dekmak"),
                new Book("and title 6", "ali somati"),
                new Book("title 1", "saad haj"),
                new Book("title 2", "mohammad abd"),
                new Book("title 3", "khaled othman"),
                new Book("title 4", "nana alhaj"),
                new Book("title 5", "abdulla dekmak"),
                new Book("and title 6", "ali somati")
        };

        mBooksRecyclerView = view.findViewById(R.id.frag_book_recycler_view);
        mBooksAdapter = new BooksCardAdapter(books);
        mBooksRecyclerView.setHasFixedSize(true);
        //todo caculate the right number of culomns using utility class defind in utils folder before setting it here
        // basically i am choosing 3
        GridLayoutManager gridBooksManagaer = new GridLayoutManager(this.getContext(), 3);
        mBooksRecyclerView.setLayoutManager(gridBooksManagaer);
        mBooksRecyclerView.setAdapter(mBooksAdapter);

        //initialize and assign values inside
        // todo here we will deal with books recyclerview
        // todo 2 plus : we should reset the adapter for every new category i dont know how but we should do it




        return view;
    }
}