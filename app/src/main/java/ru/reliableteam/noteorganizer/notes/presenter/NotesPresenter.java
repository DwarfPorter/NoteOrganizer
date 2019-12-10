package ru.reliableteam.noteorganizer.notes.presenter;

import android.view.View;
import ru.reliableteam.noteorganizer.entity.NoteDaoImpl;
import ru.reliableteam.noteorganizer.entity.shared_prefs.SharedPreferencesManager;
import ru.reliableteam.noteorganizer.notes.model.Note;
import ru.reliableteam.noteorganizer.notes.view.IViewHolder;
import ru.reliableteam.noteorganizer.notes.view.NotesRecyclerAdapter;
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

    private NotesFragment fragmentView;
    private SharedPreferencesManager appSettings;

    enum State {MULTI_SELECTION, SINGLE_SELECTION}
    private State state = State.SINGLE_SELECTION;

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
    public void notifyDatasetChanged(int messageId) {
        if (messageId == 0)
            fragmentView.notifyDataChanged();
        else
            fragmentView.showToast(messageId);
    }

    @Override
    public void bindView(IViewHolder viewHolder) {
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
        if (state == State.SINGLE_SELECTION)
            viewNote(position);
        else
            selectNote(position);
    }
    @Override
    public void createNewNote() {
        disableSort();
        disableMultiSelection();
        appSettings.setClickedNoteId(NEW_NOTE);
        fragmentView.viewNote();
    }

    private void viewNote(int position) {
        if (position == NEW_NOTE)
            appSettings.setClickedNoteId(NEW_NOTE);
        else {
            Note note = notesList.get(position);
            appSettings.setClickedNoteId(note.id);
        }
        fragmentView.viewNote();
    }
    private void selectNote(int position) {
        Note note = notesList.get(position);
        if (selectedNotes.contains(note)) {
            selectedNotes.remove(note);
            fragmentView.setSelected(false, position);
        }
        else {
            selectedNotes.add(note);
            fragmentView.setSelected(true, position);
        }
    }

    @Override
    public void longClicked(int position, View v) {
        if (state != State.MULTI_SELECTION) {
            enableMultiSelection();
            selectNote(position);
        }
    }

    @Override
    public void searchNotes(String s) {
        if (s.length() == 0)
            getNotes();
        else
            search(this, s);
    }

    @Override
    public void enableSort(){
        disableMultiSelection();
        fragmentView.setSortLayoutVisibility(true);
        fragmentView.setExtraOptionsLayoutVisibility(false);
        changeStateTo(State.SINGLE_SELECTION);
        updateByState();
    }

    @Override
    public void disableSort() {
        fragmentView.setSortLayoutVisibility(false);
        changeStateTo(State.SINGLE_SELECTION);
        updateByState();
    }

    @Override
    public void enableMultiSelection() {
        disableSort();
        fragmentView.setExtraOptionsLayoutVisibility(true);
        changeStateTo(State.MULTI_SELECTION);
    }

    @Override
    public void disableMultiSelection() {
        fragmentView.setExtraOptionsLayoutVisibility(false);
        changeStateTo(State.SINGLE_SELECTION);
        fragmentView.toDefaultStyle();
        updateByState();
    }

    private void changeStateTo(State newState) {
        state = newState;
    }
    private void updateByState() {
        if (state == State.SINGLE_SELECTION) {
            selectedNotes.clear();
        }
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();
    }

    @Override
    public void deleteNotes() {
        for (Note note : selectedNotes)
            deleteSelectedNote(this, note);
    }

    @Override
    public void migrateSelectedNotes() {
        migrateSelected(this);
    }
}