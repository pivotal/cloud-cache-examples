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
import NotesForm from "./NotesForm";
import sessionService from "../sessionService";
import NotesDisplay from "./NotesDisplay";
import '../styles/main.css';

class App extends Component {
    constructor(props) {
        super(props);
        this.state = {notes: []};
    }

    getNotes = async () => {
        const notes = await sessionService.getNotes();
        this.setState({notes})
    };

    async componentDidMount() {
        await this.getNotes();
    }

    render() {
        return (
            <div className="App" data-qa="App">
                <h1>Session State Caching</h1>
                <NotesForm getNotes={this.getNotes}/>
                <NotesDisplay getNotes={this.getNotes} notes={this.state.notes}/>
            </div>
        );
    }
}

export default App;
