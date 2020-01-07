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

import sessionService from "../src/sessionService";
import mockAxios from 'axios'


describe("sessionService", () => {
    it('should call addSessionNote endpoint when addNote is called', async () => {
        mockAxios.post.mockImplementationOnce(() =>
            Promise.resolve({ data: {} }),
        );
        await sessionService.addNote("New note!");

        expect(mockAxios.post).toHaveBeenCalledTimes(1);
        expect(mockAxios.post).toHaveBeenCalledWith("/addSessionNote", 'New note!', {"headers": {"Content-Type": "text/plain"}});
    });

    it('should call getSessionNotes endpoint when getNotes is called', async () => {
        mockAxios.get.mockImplementationOnce(() =>
            Promise.resolve({ data: ["One note", "Two note"] }),
        );
        await sessionService.getNotes();
        expect(mockAxios.get).toHaveBeenCalledTimes(1);
        expect(mockAxios.get).toHaveBeenCalledWith("/getSessionNotes");
    });

    it('should call invalidateSession endpoint when destroySession is called', async () => {
        mockAxios.post.mockImplementationOnce(() =>
            Promise.resolve({ data: {} }),
        );
        await sessionService.destroySession();

        expect(mockAxios.post).toHaveBeenCalledTimes(1);
        expect(mockAxios.post).toHaveBeenCalledWith("/invalidateSession");
    });
});