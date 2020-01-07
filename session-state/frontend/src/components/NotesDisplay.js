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

import React from 'react';
import propTypes from "prop-types";
import sessionService from "../sessionService";

const NotesDisplay = (props) => {
    const notes = Array.from(props.notes).map((note, index) => {
        return <p key={`note${index}`} data-qa="NotesDisplay__note">{note}</p>});

    const destroySession = async () => {
        await sessionService.destroySession();
        await props.getNotes();
    };

    if (notes.length > 0) {
        return (
                <div>
                    <div className="NotesDisplay__header-wrapper">
                        <h2 data-qa="NotesDisplay__header">Notes</h2>
                        <button className="NotesDisplay__destroy-session-button"
                                data-qa="NotesDisplay__destroy-session-button"
                                onClick={destroySession}>
                            DESTROY SESSION
                        </button>
                    </div>
                    {notes}
                </div>
            );
    }

    return null;
};

export default NotesDisplay;

NotesDisplay.propTypes = {
    notes: propTypes.array,
    getNotes: propTypes.func,
};