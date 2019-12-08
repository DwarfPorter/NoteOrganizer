package ru.reliableteam.noteorganizer.notes.presenter;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import ru.reliableteam.noteorganizer.NoteDaoImpl;
import ru.reliableteam.noteorganizer.entity.shared_prefs.SharedPreferencesManager;
import ru.reliableteam.noteorganizer.notes.model.Note;
import ru.reliableteam.noteorganizer.notes.view.MyAdapter;
import ru.reliableteam.noteorganizer.notes.view.NotesFragment;

/**
 * Base Notes Presenter
 *
 * Seems it has to implement BasePresenter interface and it should be given to Adapter class
 * to interact with data.
 *
 * Generates sample data.
 * Methods to add:
 *  -   database (insert, add, update, delete)
 *  -   async data getting and setting
 */


public class NotesPresenter extends NoteDaoImpl implements INotesPresenter {
    private String CLASS_TAG = "RecyclerViewPresenter";
    private final int NEW_NOTE = -1;

//    private Context context;
    private NotesFragment fragmentView;
    private SharedPreferencesManager appSettings;

    public NotesPresenter(NotesFragment view) {
        this.fragmentView = view;
        appSettings = getAppSettings();
        getNotes();
    }

    public void getNotes() {
        String searchText = fragmentView.getSearchText();
        if (searchText.equals(""))
            getFromDB(this);
        else
            searchNotes(searchText);

    }

    @Override
    public void notifyDatasetChanged() { fragmentView.notifyDataChanged(); }

    @Override
    public void bindView(MyAdapter.MyViewHolder viewHolder) {
        int position = viewHolder.getPos();
        Note note = notesList.get(position);
        viewHolder.setNote(note);
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    @Override
    public void clicked(int position) {
        if (position == NEW_NOTE)
            appSettings.setClickedNoteId(NEW_NOTE);
        else {
            Note note = notesList.get(position);
            appSettings.setClickedNoteId(note.id);
        }
        fragmentView.viewNote();
    }

    @Override
    public void searchNotes(String s) {
        if (s.length() == 0)
            getNotes();
        else
            search(this, s);
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();
    }

}