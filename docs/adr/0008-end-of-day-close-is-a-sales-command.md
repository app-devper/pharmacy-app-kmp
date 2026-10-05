# End-of-day close is a Sales command

An end-of-day close must create a durable record of the covered period, responsible user, and close time. Sales owns this command; Reporting reads and displays the resulting record. KMP currently calls a close endpoint that the pharmacy API does not expose, so the UI and backend contract must be aligned before the action can be considered available.
