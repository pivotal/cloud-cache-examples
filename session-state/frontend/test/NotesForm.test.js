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
import {mount} from 'enzyme';
import NotesForm from "../src/components/NotesForm";
import sessionService from "../src/sessionService";

describe("NotesForm", () => {
    let subject;
    const mockGetNotes = jest.fn();

    beforeEach(() => {
        subject = mount(
            <NotesForm getNotes={mockGetNotes} />
        );
    });

    it('should capture note correctly when input text changes', () => {
        expect(subject.state().note).toEqual('');

        const noteInput = subject.find('[data-qa="NotesForm__note-input"]');
        noteInput.instance().value = 'New note!';
        noteInput.simulate('change');

        expect(subject.state().note).toEqual('New note!');
    });

    it('should add inputted note when form is submitted', () => {

        const addNoteSpy = jest.spyOn(sessionService, 'addNote');
        const noteInput = subject.find('[data-qa="NotesForm__note-input"]');

        noteInput.instance().value = 'New note!';
        noteInput.simulate('change');
        subject.find('[data-qa="NotesForm__submit-button"]').simulate('submit');

        expect(addNoteSpy).toHaveBeenCalledTimes(1);
        expect(addNoteSpy).toHaveBeenCalledWith('New note!');
    });
});