package ru.reliableteam.noteorganizer.notes.presenter;

import ru.reliableteam.noteorganizer.entity.NoteDaoImpl;
import ru.reliableteam.noteorganizer.entity.BasePresenter;
import ru.reliableteam.noteorganizer.entity.shared_prefs.SharedPreferencesManager;
import ru.reliableteam.noteorganizer.notes.view.SingleNoteActivity;

public class SingleNotePresenter extends NoteDaoImpl implements BasePresenter {
    private SingleNoteActivity view;
    private SharedPreferencesManager appSettings;
    private final int NEW_NOTE = -1;

    public SingleNotePresenter (SingleNoteActivity activity) {
        this.view = activity;
        appSettings = getAppSettings();
    }

    public void getClickedNote() {
        int id = appSettings.getClickedNoteId();
        if (id != NEW_NOTE) {
            getNote(id, this);
        }
    }

    @Override
    public void notifyDatasetChanged(int messageId) {
        view.setNoteText(note.body);
        view.setNoteTitle(note.title);
    }

    public boolean isNewNote() { return appSettings.getClickedNoteId() == NEW_NOTE; }

    public void deleteNote() {
        super.deleteNote(note);
    }
    public void saveNote() {
        if (isNewNote()) {
            note.body = view.getNoteText();
            note.title = view.getNoteTitle();
            note.dataTime = System.currentTimeMillis();
            super.saveNote(note);
        }
        else
            super.updateNote(note);
    }
}
