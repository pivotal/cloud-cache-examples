/*
 * Copyright (C) 2019-Present Pivotal Software, Inc. All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the under the Apache License, Version
 * 2.0 (the "License”); you may not use this file except in compliance with the License. You may obtain a copy of the
 * License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

import React, {Component} from 'react';
import sessionService from "../sessionService";
import propTypes from "prop-types";

class NotesForm extends Component {
    constructor(props) {
        super(props);
        this.state = {note: ''};
    }

    addNote = async (event) => {
        event.preventDefault();
        await sessionService.addNote(this.state.note.valueOf());
        this.setState({note: ''});
        await this.props.getNotes();

    };

    onNoteInputChange = (event) => {
        this.setState({note: event.target.value});
    };

    render() {
        return (
            <form className="NotesForm" onSubmit={this.addNote}>
                <label className="NotesForm__note-label">Enter your note:</label>
                <input
                    className="NotesForm__note-input"
                    data-qa="NotesForm__note-input"
                    type='text'
                    value={this.state.note}
                    onChange={this.onNoteInputChange}
                />
                <button
                    className="NotesForm__submit-button"
                    data-qa="NotesForm__submit-button"
                    type="submit">Add</button>
            </form>
        );
    }
}

export default NotesForm;

NotesForm.propTypes = {
    getNotes: propTypes.func
};