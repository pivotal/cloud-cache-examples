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
import NotesDisplay from "../src/components/NotesDisplay";
import sessionService from "../src/sessionService";

describe("NotesDisplay", () => {
    let subject;

    beforeEach(() => {
        subject = mount(
            <NotesDisplay notes={["One note", "Two note", "Red note", "Blue note"]} getNotes={() => {}}/>
        );
    });

    it('should not display any notes or notes header if no notes exist', () => {
        subject.setProps({notes: []});

        expect(subject.find('[data-qa="NotesDisplay__header"]').exists()).toBeFalsy();
    });

    it('should correctly display notes if notes exist', () => {
        const notes = subject.find('[data-qa="NotesDisplay__note"]');

        expect(notes.length).toEqual(4);
        expect(notes.at(0).text()).toEqual("One note");
        expect(subject.find('[data-qa="NotesDisplay__header"]').exists()).toBeTruthy();
    });

    it('should destroy session when "Destroy Session" button clicked', () => {
        const destroySessionSpy = jest.spyOn(sessionService, 'destroySession');

        subject.find('[data-qa="NotesDisplay__destroy-session-button"]').simulate('click');

        expect(destroySessionSpy).toHaveBeenCalledTimes(1);
    });
});
