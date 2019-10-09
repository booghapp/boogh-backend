import React from 'react';
import { DropdownItem } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { NavLink as Link } from 'react-router-dom';
import { NavDropdown } from '../header-components';

export const EntitiesMenu = props => (
    // tslint:disable-next-line:jsx-self-close
    <NavDropdown icon="th-list" name="Entities" id="entity-menu">
        <DropdownItem tag={Link} to="/entity/report">
            <FontAwesomeIcon icon="asterisk" fixedWidth />
            &nbsp;Report
        </DropdownItem>
        <DropdownItem tag={Link} to="/entity/report-status">
            <FontAwesomeIcon icon="asterisk" fixedWidth />
            &nbsp;Report Status
        </DropdownItem>
        <DropdownItem tag={Link} to="/entity/reporter">
            <FontAwesomeIcon icon="asterisk" fixedWidth />
            &nbsp;Reporter
        </DropdownItem>
        <DropdownItem tag={Link} to="/entity/vote">
            <FontAwesomeIcon icon="asterisk" fixedWidth />
            &nbsp;Vote
        </DropdownItem>
        <DropdownItem tag={Link} to="/entity/comment">
            <FontAwesomeIcon icon="asterisk" fixedWidth />
            &nbsp;Comment
        </DropdownItem>
        <DropdownItem tag={Link} to="/entity/honk">
            <FontAwesomeIcon icon="asterisk" fixedWidth />
            &nbsp;Honk
        </DropdownItem>
        <DropdownItem tag={Link} to="/entity/document">
            <FontAwesomeIcon icon="asterisk" fixedWidth />
            &nbsp;Document
        </DropdownItem>
        <DropdownItem tag={Link} to="/entity/qa">
            <FontAwesomeIcon icon="asterisk" fixedWidth />
            &nbsp;Qa
        </DropdownItem>
        <DropdownItem tag={Link} to="/entity/telegram-chat">
            <FontAwesomeIcon icon="asterisk" fixedWidth />
            &nbsp;Telegram Chat
        </DropdownItem>
        <DropdownItem tag={Link} to="/entity/feedback">
            <FontAwesomeIcon icon="asterisk" fixedWidth />
            &nbsp;Feedback
        </DropdownItem>
        <DropdownItem tag={Link} to="/entity/memcache">
            <FontAwesomeIcon icon="asterisk" fixedWidth />
            &nbsp;Memcache
        </DropdownItem>
        {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </NavDropdown>
);
